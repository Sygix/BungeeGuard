package net.uhcwork.BungeeGuard.Managers;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
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
 * Part of net.uhcwork.BungeeGuard.Lobbies (bungeeguard)
 * Date: 20/09/2014
 * Time: 22:48
 * May be open-source & be sold (by mguerreiro, of course !)
 */
public class LobbyManager {
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
    private final Main plugin;

    @Getter
    private List<Lobby> lobbies = new ArrayList<>();

    public LobbyManager(Main plugin) {
        this.plugin = plugin;
    }

    public Lobby getLobby(String servername) {
        for (Lobby Lobby : getLobbies()) {
            if (Lobby.getName().equals(servername))
                return Lobby;
        }
        return null;
    }

    public void setupPingTask() {
        ProxyServer.getInstance().getScheduler().schedule(plugin, new Runnable() {
            @Override
            public void run() {
                final List<Lobby> new_lobbies = new ArrayList<>();
                for (final ServerInfo serverInfo : ProxyServer.getInstance().getServers().values()) {
                    if (serverInfo.getName().startsWith("lobby") || serverInfo.getName().startsWith("limbo")) {
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
    public Lobby getBestLobbyFor(ProxiedPlayer p) {
        Collection<Lobby> lobbies = Collections2.filter(getLobbies(), isOnline);
        Ordering<Lobby> scoreOrdering = Ordering.natural().onResultOf(getScoreFunction);
        ImmutableSortedSet<Lobby> sortedLobbies = ImmutableSortedSet.orderedBy(scoreOrdering).addAll(lobbies).build();

        return sortedLobbies.descendingSet().first();
    }

    @Data
    public static class Lobby {
        private final String name = "";
        private final int onlinePlayers = 0;
        private final int maxPlayers = 10;
        private final double tps = 0;
        @Getter(lazy = true)
        private final Double score = score();
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

        public void setOffline() {
            setOnline(false);
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
