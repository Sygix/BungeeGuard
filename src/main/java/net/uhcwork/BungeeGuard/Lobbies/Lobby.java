package net.uhcwork.BungeeGuard.Lobbies;

import lombok.Getter;
import lombok.Setter;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;

public class Lobby {
    @Getter
    @Setter
    private String name = "";
    @Getter
    @Setter
    private int onlinePlayers = 0;
    @Getter
    @Setter
    private int maxPlayers = 10;
    @Getter
    private boolean isOnline = false;
    @Getter
    @Setter
    private double tps = 0;
    @Setter
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

    public ServerInfo getServerInfo() {
        return ProxyServer.getInstance().getServerInfo(this.name);
    }

    public void setOnline(boolean isOnline) {
        this.isOnline = isOnline;
    }

    public void setOffline() {
        setOnline(false);
    }

    public double getScore() {
        if (score == null) {
            if (getName().startsWith("limbo")) {
                score = -Double.MAX_VALUE;
            } else {
                double _score = (1 + getOnlinePlayers()) * (getMaxPlayers() / 2 - getOnlinePlayers());
                if (_score > 0) {
                    score = _score * getTps();
                } else {
                    score = _score * (20 - getTps());
                }
            }
        }
        return score;
    }
}
