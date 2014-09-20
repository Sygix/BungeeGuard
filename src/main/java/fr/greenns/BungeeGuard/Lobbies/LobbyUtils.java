package fr.greenns.BungeeGuard.Lobbies;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import fr.greenns.BungeeGuard.Main;

import java.util.Collection;

public class LobbyUtils {

    public Main plugin;
    Predicate<Lobby> isOnline = new Predicate<Lobby>() {
        public boolean apply(Lobby lobby) {
            return lobby != null && lobby.isOnline();
        }
    };

    public LobbyUtils(Main plugin) {
        this.plugin = plugin;
    }

    public Lobby getLobby(String servername) {
        for (Lobby Lobby : plugin.lobbys) {
            if (Lobby.getName().equals(servername))
                return Lobby;
        }
        return null;
    }

    public Lobby bestLobbyTarget() {
        Collection<Lobby> lobbies = Collections2.filter(plugin.lobbys, isOnline);
        for (int tps = 20; tps > 0; tps -= 1)
            for (int maxPlayers = 0; maxPlayers < 60; maxPlayers += 15)
                for (Lobby lobby : lobbies)
                    if (lobby.getTps() >= tps && lobby.getNbrPlayers() <= maxPlayers)
                        return lobby;
        return null;
    }
}
