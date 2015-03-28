package fr.PunKeel.BungeeGuard.Managers;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableSortedSet;
import com.google.common.collect.Ordering;
import com.google.gson.Gson;
import fr.PunKeel.BungeeGuard.Main;
import fr.PunKeel.BungeeGuard.Models.BungeeServer;
import fr.PunKeel.BungeeGuard.Permissions.Group;
import fr.PunKeel.BungeeGuard.Permissions.Permissions;
import lombok.Getter;
import net.md_5.bungee.api.Callback;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.ServerPing;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.io.Serializable;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class ServerManager {
    private static final Map<UUID, String> lastLobby = new HashMap<>();
    static Gson gson;
    private final Main plugin;
    @Getter
    Cache<String, Optional<ServerPing>> serversCache = CacheBuilder.newBuilder()
            .maximumSize(500)
            .expireAfterWrite(3, TimeUnit.SECONDS)
            .build();
    @Getter
    private Map<String, Lobby> lobbies = new HashMap<>();
    private ServerPing restrictedPing;

    public ServerManager(Main main) {
        this.plugin = main;
        gson = Main.getGson();
        restrictedPing = new ServerPing(
                new ServerPing.Protocol("0", 0),
                new ServerPing.Players(0, 0, null),
                "{'state': 'maintenance'}",
                ProxyServer.getInstance().getConfig().getFaviconObject());
    }

    private static String getLastLobby(UUID uuid) {
        return lastLobby.containsKey(uuid) ? lastLobby.get(uuid) : "";
    }

    private Predicate<Lobby> isOnline(final ProxiedPlayer p) {
        return new Predicate<Lobby>() {
            public boolean apply(Lobby lobby) {
                return lobby != null && lobby.isOnline() && lobby.getServerInfo().canAccess(p);
            }
        };
    }

    public Collection<String> matchServer(final String serverName) {
        Collection<String> serverNames = new ArrayList<>();
        for (String _name : plugin.getConfig().getServers().keySet()) {
            if (Permissions.miniglob(serverName, _name)) {
                serverNames.add(_name);
            }
        }
        return serverNames;
    }

    public void ping(final String serverName, final Callback<ServerPing> pingBack) {
        if (serverName == null || serverName.isEmpty())
            return;
        ServerInfo server = ProxyServer.getInstance().getServerInfo(serverName);
        if (server == null)
            return;
        final Optional<ServerPing> SP = getServersCache().getIfPresent(serverName);

        if (SP == null) {
            if (isRestricted(serverName)) {
                if (pingBack != null)
                    pingBack.done(restrictedPing, null);
                return;
            }
            final Callback<ServerPing> pingCallback = new Callback<ServerPing>() {
                @Override
                public void done(ServerPing serverPing, Throwable throwable) {
                    Optional<ServerPing> serverPingOptional = Optional.fromNullable(serverPing);
                    getServersCache().put(serverName, serverPingOptional);
                    if (pingBack != null)
                        pingBack.done(serverPing, throwable);
                }
            };
            server.ping(pingCallback);
        } else if (pingBack != null)
            pingBack.done(SP.orNull(), null);
    }

    public boolean isRestricted(String serverName) {
        Preconditions.checkArgument(ProxyServer.getInstance().getServerInfo(serverName) != null, "Server does not exist");
        return getServerModel(serverName) != null && getServerModel(serverName).isRestricted();
    }

    public String getShortName(String serverName) {
        Preconditions.checkArgument(ProxyServer.getInstance().getServerInfo(serverName) != null, "Server does not exist");
        return getServerModel(serverName).getShortName();
    }

    public String getPrettyName(String serverName) {
        Preconditions.checkArgument(ProxyServer.getInstance().getServerInfo(serverName) != null, "Server does not exist");
        return getServerModel(serverName).getPrettyName();
    }

    public void setupPingTask() {
        ProxyServer.getInstance().getScheduler().schedule(plugin, new Runnable() {
            @Override
            public void run() {
                Collection<ServerInfo> servers = new HashSet<>(ProxyServer.getInstance().getServers().values());
                for (final ServerInfo serverInfo : servers) {
                    if (isLobbyName(serverInfo.getName())) {
                        Callback<ServerPing> pingBack = new Callback<ServerPing>() {
                            @Override
                            public void done(ServerPing result, Throwable error) {
                                boolean isError = (error != null) || (result == null);
                                Lobby lobby = new Lobby(isError, serverInfo, result);
                                lobbies.put(serverInfo.getName(), lobby);
                            }
                        };
                        ping(serverInfo.getName(), pingBack);
                    }
                }
                Iterator<String> i = lobbies.keySet().iterator();
                while (i.hasNext()) {
                    // Supprime les lobbies qui ont été supprimés de la liste des serveurs
                    if (plugin.getProxy().getServerInfo(i.next()) == null)
                        i.remove();
                }
            }
        }, 1, 1, TimeUnit.SECONDS);
        ProxyServer.getInstance().getScheduler().schedule(plugin, new Runnable() {
            @Override
            public void run() {
                Collection<ServerInfo> servers = new HashSet<>(ProxyServer.getInstance().getServers().values());
                for (final ServerInfo serverInfo : servers) {
                    if (!isLobbyName(serverInfo.getName())) {
                        ping(serverInfo.getName(), null);
                    }
                }
                Iterator<Lobby> i = lobbies.values().iterator();
                while (i.hasNext()) {
                    // Remove obsolete lobbies
                    if (i.next().getServerInfo() == null)
                        i.remove();
                }
            }
        }, 1, 1, TimeUnit.SECONDS);
    }

    public String getBestLobbyFor(final ProxiedPlayer p) {
        Collection<Lobby> lobbies = Collections2.filter(getLobbies().values(), isOnline(p));
        ImmutableSortedSet<Lobby> sortedLobbies = ImmutableSortedSet.orderedBy(getScoreOrdering(p)).addAll(lobbies).build();
        if (sortedLobbies.isEmpty())
            return null;
        return sortedLobbies.first().getName();
    }

    private Ordering<Lobby> getScoreOrdering(final ProxiedPlayer p) {
        final Function<Lobby, Double> getScoreFunction = new Function<Lobby, Double>() {
            public Double apply(Lobby lobby) {
                return lobby.getScore(p);
            }
        };
        return Ordering.natural().onResultOf(getScoreFunction).reverse();
    }

    public void setOffline(String name) {
        getServersCache().put(name, Optional.<ServerPing>absent());
        if (isLobbyName(name) && lobbies.containsKey(name)) {
            lobbies.get(name).setOnline(false);
        }
    }

    private boolean isLobbyName(String name) {
        return name.startsWith("lobby") || name.startsWith("limbo");
    }

    public Collection<ServerInfo> getOnlineLobbies() {
        return Collections2.transform(Collections2.filter(getLobbies().values(), new Predicate<Lobby>() {
            @Override
            public boolean apply(Lobby lobby) {
                return lobby != null && lobby.isOnline() && lobby.getServerInfo() != null;
            }
        }), new Function<Lobby, ServerInfo>() {
            @Override
            public ServerInfo apply(Lobby lobby) {
                return lobby.getServerInfo();
            }
        });
    }

    public BungeeServer getServerModel(String serverName) {
        return plugin.getConfig().getServer(serverName);
    }

    public void addPlayer(String server, int count) {
        if (server == null)
            return;
        Optional<ServerPing> ping = getServersCache().getIfPresent(server);
        if (ping == null || !ping.isPresent())
            return;
        ServerPing.Players players = ping.get().getPlayers();
        players.setOnline(players.getOnline() + count);
    }

    public Collection<String> getServersInMaintenance() {
        return Collections2.filter(ProxyServer.getInstance().getServers().keySet(), new Predicate<String>() {
            @Override
            public boolean apply(String serverName) {
                return isRestricted(serverName);
            }
        });
    }

    public void setRestricted(String serverName, boolean restricted) {
        getServerModel(serverName).setRestricted(restricted);
    }

    public boolean isLobby(ServerInfo info) {
        return isLobbyName(info.getName());
    }

    public boolean setLastLobby(UUID uniqueId, ServerInfo target) {
        if (target.getName().equals(getLastLobby(uniqueId)))
            return false;
        lastLobby.put(uniqueId, target.getName());
        return true;
    }

    public void resetLastLobby(UUID uuid) {
        lastLobby.remove(uuid);
    }

    public Map<String, LobbyInfo> getLobbiesInfo() {
        Map<String, LobbyInfo> servers = new HashMap<>();
        for (Map.Entry<String, ServerManager.Lobby> entry : Main.getServerManager().getLobbies().entrySet()) {
            servers.put(entry.getKey(), new LobbyInfo(entry.getValue()));
        }
        return servers;
    }


    public static class Lobby implements Serializable {
        @Getter
        private final ServerPing result;
        @Getter
        private String name = "";
        @Getter
        private boolean isOnline = false;
        private int maxPlayers = 10;

        public Lobby(boolean isError, ServerInfo serverInfo, ServerPing result) {
            isOnline = !isError;
            if (isError) {
                this.result = null;
                return;
            }
            name = serverInfo.getName();
            maxPlayers = result.getPlayers().getMax();
            this.result = result;
        }

        private static boolean isLimboName(String name) {
            return name.startsWith("limbo");
        }

        @Override
        public String toString() {
            return "Lobby{" +
                    "name='" + name + '\'' +
                    ", maxPlayers=" + maxPlayers +
                    ", isOnline=" + isOnline +
                    '}';
        }

        public ServerInfo getServerInfo() {
            return ProxyServer.getInstance().getServerInfo(this.name);
        }

        public void setOnline(boolean isOnline) {
            this.isOnline = isOnline;
        }

        public double getScore(ProxiedPlayer p) {
            double target = .6;
            // With a target of .6, the lobbies will be filled at 60% before sending people to another one.
            double bonus = 0;
            if (p != null && getLastLobby(p.getUniqueId()) != null)
                if (getLastLobby(p.getUniqueId()).equals(name))
                    bonus = 1000;
            if (isLimboName(name)) {
                return -Double.MAX_VALUE;
            }
            int onlinePlayers = result.getPlayers().getOnline();
            return (1 + onlinePlayers) * (maxPlayers * target - onlinePlayers) + bonus;
        }
    }

    private class LobbyInfo {
        String name = "";
        boolean isOnline = false;
        int maxPlayers = 0;
        int onlinePlayers = 0;
        int port = 0;
        Map<String, Integer> ranks = new TreeMap<>();


        public LobbyInfo(ServerManager.Lobby lobby) {
            name = lobby.getName();
            port = lobby.getServerInfo().getAddress().getPort();
            isOnline = lobby.isOnline();
            if (!isOnline)
                return;
            maxPlayers = lobby.getResult().getPlayers().getMax();
            onlinePlayers = lobby.getResult().getPlayers().getOnline();
            Map<Group, Integer> _ranks = new TreeMap<>(new Comparator<Group>() {
                public int compare(Group o1, Group o2) {
                    Integer weight1 = o1.getWeight();
                    Integer weight2 = o2.getWeight();
                    return -1 * weight1.compareTo(weight2); // inverted order
                }
            });
            for (UUID uuid : Main.getMB().getPlayersOnServer(name)) {
                Group main = plugin.getPermissionManager().getMainGroup(uuid);
                int online = _ranks.containsKey(main) ? _ranks.get(main) : 0;
                _ranks.put(main, online + 1);
            }
            for (Group g : _ranks.keySet()) {
                String displayName = g.getColor() + g.getName();
                if (g.hasPermission("lobby.vanishjoin"))
                    continue;

                if (Objects.equals(g.getId(), "default")) {
                    displayName = ChatColor.GRAY + "Joueurs";
                }
                // Used to merge common groups
                ranks.put(displayName, (ranks.containsKey(displayName) ? ranks.get(displayName) : 0) + _ranks.get(g));
            }
        }
    }
}
