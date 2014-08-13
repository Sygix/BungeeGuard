package fr.greenns.BungeeGuard.utils;

import java.util.UUID;

import fr.greenns.BungeeGuard.BungeeGuard;

public class AuthPlayer {
	private UUID UUID;
	private String secretKey;
	private boolean logged;
	
	public AuthPlayer(UUID UUID, String secretKey) {
		this.UUID = UUID;
		this.secretKey = secretKey;
		this.logged = false;
		BungeeGuard.authplayers.add(this);
	}

	public UUID getUUID() {
		return UUID;
	}

	public String getSecretKey() {
		return secretKey;
	}

	public boolean isLogged() {
		return logged;
	}
	
	public void setLogged() {
		logged = true;;
	}
}
