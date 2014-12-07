package net.uhcwork.BungeeGuard.Managers;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableSortedSet;
import com.google.common.collect.Ordering;
import com.google.gson.Gson;
import lombok.Getter;
import net.md_5.bungee.api.Callback;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.ServerPing;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.uhcwork.BungeeGuard.Main;
import net.uhcwork.BungeeGuard.Models.BungeeServer;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Part of net.uhcwork.BungeeGuard.Lobbies (BungeeGuard)
 * Date: 12/10/2014
 * Time: 13:55
 * May be open-source & be sold (by mguerreiro, of course !)
 */
public class ServerManager {
    static Gson gson;
    private final Main plugin;
    private final Function<Lobby, Double> getScoreFunction = new Function<Lobby, Double>() {
        public Double apply(Lobby lobby) {
            return lobby.getScore();
        }
    };
    @Getter
    Cache<String, Optional<ServerPing>> serversCache = CacheBuilder.newBuilder()
            .maximumSize(500)
            .expireAfterWrite(1, TimeUnit.SECONDS)
            .build();
    @Getter
    private Map<String, Lobby> lobbies = new HashMap<>();

    public ServerManager(Main main) {
        this.plugin = main;
        gson = Main.getGson();
    }

    private Predicate<Lobby> isOnline(final ProxiedPlayer p) {
        return new Predicate<Lobby>() {
            public boolean apply(Lobby lobby) {
                return lobby != null && lobby.isOnline() && lobby.getServerInfo().canAccess(p);
            }
        };
    }

    public void ping(final String serverName, final Callback<ServerPing> pingBack) {
        if (serverName == null || serverName.isEmpty())
            return;
        final Optional<ServerPing> SP = getServersCache().getIfPresent(serverName);

        if (SP == null) {
            final Callback<ServerPing> pingCallback = new Callback<ServerPing>() {
                @Override
                public void done(ServerPing serverPing, Throwable throwable) {
                    if (plugin.isRestricted(serverName)) {
                        serverPing.setDescription("{'state': 'maintenance'}");
                        serverPing.setPlayers(new ServerPing.Players(0, 0, null));
                    }
                    Optional<ServerPing> serverPingOptional = Optional.fromNullable(serverPing);
                    getServersCache().put(serverName, serverPingOptional);
                    pingBack.done(serverPing, throwable);
                }
            };
            ProxyServer.getInstance().getServerInfo(serverName).ping(pingCallback);
        } else {
            pingBack.done(SP.orNull(), null);
        }
    }

    public void setupPingTask() {
        ProxyServer.getInstance().getScheduler().schedule(plugin, new Runnable() {
            @Override
            public void run() {
                for (final ServerInfo serverInfo : ProxyServer.getInstance().getServers().values()) {
                    if (isLobbyName(serverInfo.getName())) {
                        Callback<ServerPing> pingBack = new Callback<ServerPing>() {
                            @Override
                            public void done(ServerPing result, Throwable error) {
                                boolean isError = (error != null) || (result == null);
                                Lobby lobby = new Lobby(isError, serverInfo, result);
                                lobbies.put(serverInfo.getName(), lobby);
                            }
                        };
                        plugin.getServerManager().ping(serverInfo.getName(), pingBack);
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
    }

    @SuppressWarnings("UnusedParameters")
    public String getBestLobbyFor(final ProxiedPlayer p) {
        Collection<Lobby> lobbies = Collections2.filter(getLobbies().values(), isOnline(p));
        Ordering<Lobby> scoreOrdering = Ordering.natural().onResultOf(getScoreFunction);
        ImmutableSortedSet<Lobby> sortedLobbies = ImmutableSortedSet.orderedBy(scoreOrdering).addAll(lobbies).build().descendingSet();
        if (sortedLobbies.size() == 0)
            return null;
        return sortedLobbies.first().getName();
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
                return lobby != null && lobby.isOnline();
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

    public static class Lobby {
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

        public double getScore() {
            if (isLimboName(name)) {
                return -Double.MAX_VALUE;
            }
            int onlinePlayers = result.getPlayers().getOnline();
            return (1 + onlinePlayers) * (maxPlayers / 2 - onlinePlayers);
        }
    }
}
