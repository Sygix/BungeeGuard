package net.uhcwork.BungeeGuard.Managers;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableSortedSet;
import com.google.common.collect.Ordering;
import lombok.Data;
import lombok.Getter;
import net.md_5.bungee.api.Callback;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.ServerPing;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.uhcwork.BungeeGuard.Main;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Part of net.uhcwork.BungeeGuard.Lobbies (BungeeGuard)
 * Date: 12/10/2014
 * Time: 13:55
 * May be open-source & be sold (by mguerreiro, of course !)
 */
public class ServerManager {
    private final Main plugin;
    private final Predicate<Lobby> isOnline = new Predicate<Lobby>() {
        public boolean apply(Lobby lobby) {
            return lobby != null && lobby.isOnline();
        }
    };
    private final Function<Lobby, Double> getScoreFunction = new Function<Lobby, Double>() {
        public Double apply(Lobby lobby) {
            return lobby.getScore();
        }
    };
    @Getter
    Cache<String, Optional<ServerPing>> serversCache = CacheBuilder.newBuilder()
            .maximumSize(300)
            .expireAfterWrite(1, TimeUnit.SECONDS)
            .build();
    @Getter
    private List<Lobby> lobbies = new ArrayList<>();


    public ServerManager(Main main) {
        this.plugin = main;
    }

    public void ping(final String serverName, final Callback<ServerPing> pingBack) {
        if (serverName == null || serverName.isEmpty())
            return;
        final Optional<ServerPing> SP = getServersCache().getIfPresent(serverName);

        if (SP == null) {
            final Callback<ServerPing> pingCallback = new Callback<ServerPing>() {
                @Override
                public void done(ServerPing serverPing, Throwable throwable) {
                    Optional<ServerPing> serverPingOptional = Optional.fromNullable(serverPing);
                    getServersCache().put(serverName, serverPingOptional);
                    pingBack.done(serverPing, throwable);
                }
            };
            ProxyServer.getInstance().getServerInfo(serverName).ping(pingCallback);
        } else {
            pingBack.done(SP.get(), null);
        }
    }

    public void setupPingTask() {
        ProxyServer.getInstance().getScheduler().schedule(plugin, new Runnable() {
            @Override
            public void run() {
                final List<Lobby> new_lobbies = new ArrayList<>();
                for (final ServerInfo serverInfo : ProxyServer.getInstance().getServers().values()) {
                    if (isLobbyName(serverInfo.getName())) {
                        Callback<ServerPing> pingBack = new Callback<ServerPing>() {
                            @Override
                            public void done(ServerPing result, Throwable error) {
                                Lobby lobby = new Lobby();
                                lobby.setOnline(error == null);
                                if (error == null) {
                                    lobby.setName(serverInfo.getName());
                                    lobby.setMaxPlayers(result.getPlayers().getMax());
                                    lobby.setOnlinePlayers(result.getPlayers().getOnline());
                                    if (!serverInfo.getName().startsWith("limbo")) {
                                        double tps;
                                        try {
                                            tps = Double.parseDouble(result.getDescription());
                                        } catch (NumberFormatException e) {
                                            tps = 10;
                                        }
                                        lobby.setTps(tps);
                                    }
                                }
                                new_lobbies.add(lobby);
                            }
                        };
                        plugin.getServerManager().ping(serverInfo.getName(), pingBack);
                    }
                }
                lobbies = new_lobbies;
            }
        }, 1, 3, TimeUnit.SECONDS);
    }

    @SuppressWarnings("UnusedParameters")
    public String getBestLobbyFor(ProxiedPlayer p) {
        Collection<Lobby> lobbies = Collections2.filter(getLobbies(), isOnline);
        Ordering<Lobby> scoreOrdering = Ordering.natural().onResultOf(getScoreFunction);
        ImmutableSortedSet<Lobby> sortedLobbies = ImmutableSortedSet.orderedBy(scoreOrdering).addAll(lobbies).build().descendingSet();
        return sortedLobbies.first().getName();
    }

    public void setOffline(String name) {
        getServersCache().put(name, Optional.<ServerPing>absent());
        if (isLobbyName(name)) {
            for (Lobby l : getLobbies()) {
                if (l.getName().equals(name)) {
                    l.setOnline(false);
                }
            }
        }
    }

    private boolean isLobbyName(String name) {
        return name.startsWith("lobby") || name.startsWith("limbo");
    }

    public Collection<ServerInfo> getOnlineLobbies() {
        return Collections2.transform(Collections2.filter(getLobbies(), new Predicate<Lobby>() {
            @Override
            public boolean apply(Lobby lobby) {
                return lobby.isOnline();
            }
        }), new Function<Lobby, ServerInfo>() {
            @Override
            public ServerInfo apply(Lobby lobby) {
                return lobby.getServerInfo();
            }
        });
    }

    @Data
    public static class Lobby {
        @Getter(lazy = true)
        private final Double score = score();
        private String name = "";
        private int onlinePlayers = 0;
        private int maxPlayers = 10;
        private double tps = 0;
        private boolean isOnline = false;

        public Lobby() {
        }

        @Override
        public String toString() {
            return "Lobby{" +
                    "name='" + name + '\'' +
                    ", onlinePlayers=" + onlinePlayers +
                    ", maxPlayers=" + maxPlayers +
                    ", isOnline=" + isOnline +
                    ", tps=" + tps +
                    '}';
        }

        public ServerInfo getServerInfo() {
            return ProxyServer.getInstance().getServerInfo(this.name);
        }

        public void setOnline(boolean isOnline) {
            this.isOnline = isOnline;
        }

        public double score() {
            if (getName().startsWith("limbo")) {
                return -Double.MAX_VALUE;
            }
            double _score = (1 + getOnlinePlayers()) * (getMaxPlayers() / 2 - getOnlinePlayers());
            if (_score > 0)
                return _score * getTps();
            else
                return _score * (20 - getTps());
        }
    }
}
