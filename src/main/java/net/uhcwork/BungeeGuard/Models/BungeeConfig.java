package net.uhcwork.BungeeGuard.Models;

import net.md_5.bungee.api.ChatColor;
import org.javalite.activejdbc.Model;
import org.javalite.activejdbc.annotations.Table;

@Table("bungee_config")
public class BungeeConfig extends Model {

    public String getMotd() {
        return ChatColor.translateAlternateColorCodes('&', getString("motd"));
    }

    public Integer getMaxPlayers() {
        return getInteger("max_players");
    }

    public int getBroadcastDelay() {
        return getInteger("broadcast_delay");
    }
}
