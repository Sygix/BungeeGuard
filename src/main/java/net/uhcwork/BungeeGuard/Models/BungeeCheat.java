package net.uhcwork.BungeeGuard.Models;

import org.javalite.activejdbc.Model;
import org.javalite.activejdbc.annotations.Table;

import java.util.UUID;

/**
 * Part of net.uhcwork.BungeeGuard.Models (BungeeGuard)
 * Date: 12/10/2014
 * Time: 20:11
 * May be open-source & be sold (by mguerreiro, of course !)
 */
@Table("bungee_cheats")
public class BungeeCheat extends Model {
    public void setPlayerName(String playerName) {
        setString("player_name", playerName);
    }

    public void setPlayerUUID(UUID playerUUID) {
        setString("player_uuid", playerUUID.toString());
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
