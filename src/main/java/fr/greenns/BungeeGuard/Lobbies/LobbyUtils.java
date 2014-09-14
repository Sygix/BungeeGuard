package fr.greenns.BungeeGuard.Lobbies;

import fr.greenns.BungeeGuard.Main;

import java.util.List;

public class LobbyUtils {

    public Main plugin;

    public LobbyUtils(Main plugin) {
        this.plugin = plugin;
    }

    public List<Lobby> getLobbies() {
        return plugin.lobbys;
    }

    public Lobby getLobby(String servername) {
        for (Lobby Lobby : plugin.lobbys) {
            if (Lobby.getName().equals(servername)) return Lobby;
        }
        return null;
    }

    public Lobby bestLobbyTarget() {
        for (int maxPlayers = 0; maxPlayers < 60; maxPlayers += 15) {
            for (Lobby lobby : plugin.lobbys) {
                if (lobby != null && lobby.isOnline() && lobby.getTps() >= 12 && lobby.getNbrPlayers() <= maxPlayers) {
                    return lobby;
                }
            }
        }
        return null;
    }
}
