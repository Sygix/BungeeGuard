package fr.greenns.BungeeGuard.Lobbies;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;

public class Lobby {

    private String name;
    private int players;
    private boolean online;
    private double tps;

    public Lobby(String name, int players, double tps, boolean online) {
        this.name = name;
        this.players = players;
        this.tps = tps;
        this.online = online;
    }

    public String getName() {
        return name;
    }

    public Integer getNbrPlayers() {
        return players;
    }

    public void setOffline() {
        this.online = false;
    }

    public void setOnline() {
        this.online = true;
    }

    public boolean isOnline() {
        return online;
    }

    public double getTps() {
        return tps;
    }

    public ServerInfo getServerInfo() {
        return ProxyServer.getInstance().getServerInfo(this.name);
    }
}
