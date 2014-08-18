package fr.greenns.BungeeGuard.Lobbies;

import java.util.List;

import fr.greenns.BungeeGuard.BungeeGuard;

public class LobbyUtils {

	public BungeeGuard plugin;

	public LobbyUtils(BungeeGuard plugin) {
		this.plugin = plugin;
	}

	public List<Lobby> getLobbies() {
		return BungeeGuard.lobbys;
	}

	public Lobby getLobby(String servername) {
		for(Lobby Lobby: BungeeGuard.lobbys) {
			if(Lobby.getName().equals(servername)) return Lobby;
		}
		return null;
	}
	
	public Lobby bestLobbyTarget() {
		for (int maxPlayers = 0; maxPlayers < 60; maxPlayers += 15) {
			for (Lobby Lobby : BungeeGuard.lobbys) {
				if (Lobby.isOnline() && Lobby.getTps() >= 12 && Lobby.getNbrPlayers() < maxPlayers) {
					return Lobby;
				}
			}
		}
		return null;
	}
}
