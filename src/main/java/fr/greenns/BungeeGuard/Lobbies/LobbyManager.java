package fr.greenns.BungeeGuard.Lobbies;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableSortedSet;
import com.google.common.collect.Ordering;
import fr.greenns.BungeeGuard.Main;
import net.md_5.bungee.api.Callback;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.ServerPing;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Part of fr.greenns.BungeeGuard.Lobbies (bungeeguard)
 * Date: 20/09/2014
 * Time: 22:48
 * May be open-source & be sold (by mguerreiro, of course !)
 */
public class LobbyManager {
    private Main plugin;
    private List<Lobby> lobbies = new ArrayList<>();
    Predicate<Lobby> isOnline = new Predicate<Lobby>() {
        public boolean apply(Lobby lobby) {
            return lobby != null && lobby.isOnline();
        }
    };
    Function<Lobby, Double> getScoreFunction = new Function<Lobby, Double>() {
        public Double apply(Lobby lobby) {
            return lobby.getScore();
        }
    };

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
                final List<Lobby> new_lobbys = new ArrayList<>();
                for (final ServerInfo serverInfo : ProxyServer.getInstance().getServers().values()) {
                    if (serverInfo.getName().startsWith("lobby") || serverInfo.getName().equalsIgnoreCase("limbo")) {
                        serverInfo.ping(new Callback<ServerPing>() {
                            @Override
                            public void done(ServerPing result, Throwable error) {
                                Lobby lobby = new Lobby();
                                lobby.setOnline(error == null);
                                if (error == null) {
                                    lobby.setName(serverInfo.getName());
                                    lobby.setMaxPlayers(result.getPlayers().getMax());
                                    lobby.setOnlinePlayers(result.getPlayers().getOnline());
                                    if (serverInfo.getName().equalsIgnoreCase("limbo"))
                                        lobby.setTps(-1000);
                                    else
                                        lobby.setTps(Double.parseDouble(result.getDescription()));
                                }
                                new_lobbys.add(lobby);
                            }
                        });
                    }
                }
                lobbies = new_lobbys;
            }
        }, 1, 3, TimeUnit.SECONDS);
    }

    public List<Lobby> getLobbies() {
        return lobbies;
    }

    public Lobby getBestLobbyFor(ProxiedPlayer p) {
        Collection<Lobby> lobbies = Collections2.filter(getLobbies(), isOnline);
        Ordering<Lobby> scoreOrdering = Ordering.natural().onResultOf(getScoreFunction);
        ImmutableSortedSet<Lobby> sortedLobbies = ImmutableSortedSet.orderedBy(scoreOrdering).addAll(lobbies).build();

        return sortedLobbies.descendingSet().first();
    }
}
