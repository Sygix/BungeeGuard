package net.uhcwork.BungeeGuard.Models;

import org.javalite.activejdbc.Model;
import org.javalite.activejdbc.annotations.Table;

@Table("bungee_cheats")
public class BungeeCheat extends Model {
    public void setPlayerName(String playerName) {
        setString("player_name", playerName);
    }

    public void setCheatType(String cheatType) {
        setString("cheat", cheatType);
    }

    public void setCheatScore(double cheatScore) {
        setDouble("score", cheatScore);
    }

    public void setServerName(String serverName) {
        setString("server_name", serverName);
    }
}
