package fr.greenns.BungeeGuard.Lobbies;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;

public class Lobby {

    private String name = "";
    private int onlinePlayers = 0;
    private int maxPlayers = 10;
    private boolean isOnline = false;
    private double tps = 0;
    private Double score;

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

    public String getName() {
        return name;
    }

    public void setName(String name) {

        this.name = name;
    }

    public double getTps() {
        return tps;
    }

    public void setTps(double tps) {
        this.tps = tps;
    }

    public ServerInfo getServerInfo() {
        return ProxyServer.getInstance().getServerInfo(this.name);
    }

    public int getOnlinePlayers() {
        return onlinePlayers;
    }

    public void setOnlinePlayers(int onlinePlayers) {
        this.onlinePlayers = onlinePlayers;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public void setMaxPlayers(int maxPlayers) {
        this.maxPlayers = maxPlayers;
    }

    public boolean isOnline() {
        return isOnline;
    }

    public void setOnline(boolean isOnline) {
        this.isOnline = isOnline;
    }

    public void setOffline() {
        setOnline(false);
    }

    public double getScore() {
        if (score == null)
            score = (1 + getOnlinePlayers()) * (getMaxPlayers() / 2 - getOnlinePlayers()) * getTps();
        return score;
    }
}
