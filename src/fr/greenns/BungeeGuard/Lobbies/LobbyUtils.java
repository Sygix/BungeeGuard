package fr.greenns.BungeeGuard.Lobbies;

import fr.greenns.BungeeGuard.Main;

import java.util.List;

public class LobbyUtils {

	public Main plugin;

	public LobbyUtils(Main plugin) {
		this.plugin = plugin;
	}

	public List<Lobby> getLobbies() {
		return Main.lobbys;
	}

	public Lobby getLobby(String servername) {
		for(Lobby Lobby: Main.lobbys) {
			if(Lobby.getName().equals(servername)) return Lobby;
		}
		return null;
	}
	
	public Lobby bestLobbyTarget() {
		for (int maxPlayers = 0; maxPlayers < 60; maxPlayers += 15) {
			for (Lobby Lobby : Main.lobbys) {
                if (Lobby != null && Lobby.isOnline() && Lobby.getTps() >= 12 && Lobby.getNbrPlayers() <= maxPlayers) {
					return Lobby;
				}
			}
		}
		return null;
	}
}
