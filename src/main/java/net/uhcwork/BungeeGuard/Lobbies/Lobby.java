package net.uhcwork.BungeeGuard.Lobbies;

import lombok.Data;
import lombok.Getter;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;

@Data
public class Lobby {
    private String name = "";
    private int onlinePlayers = 0;
    private int maxPlayers = 10;
    private boolean isOnline = false;
    private double tps = 0;
    @Getter(lazy = true)
    private final Double score = score();

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
