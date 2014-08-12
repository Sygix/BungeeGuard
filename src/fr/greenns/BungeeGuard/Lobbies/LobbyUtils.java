package fr.greenns.BungeeGuard.Lobbies;

import java.util.List;

import fr.greenns.BungeeGuard.BungeeGuard;

public class LobbyUtils {

	public BungeeGuard plugin;

	public LobbyUtils(BungeeGuard plugin) {
		this.plugin = plugin;
	}

	public List<Lobby> getLobbies() {
		return plugin.lobbyList;
	}

	public Lobby bestLobbyTarget() {
		for (int max = 0; max < 60;) {
			max = max + 15;
			for (Lobby l : plugin.lobbyList) {
				if (l.getSlot() < max) {
					return l;
				}
			}
		}
		return null;
	}
}
