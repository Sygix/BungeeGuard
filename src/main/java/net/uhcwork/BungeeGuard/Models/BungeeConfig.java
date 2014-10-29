package net.uhcwork.BungeeGuard.Models;

import org.javalite.activejdbc.Model;
import org.javalite.activejdbc.annotations.Table;

/**
 * Part of net.uhcwork.BungeeGuard.Models (bungeeguard)
 * Date: 20/09/2014
 * Time: 22:22
 * May be open-source & be sold (by mguerreiro, of course !)
 */
@Table("bungee_config")
public class BungeeConfig extends Model {

    public String getMotd() {
        return getString("motd");
    }

    public Integer getMaxPlayers() {
        return getInteger("max_players");
    }

    public int getBroadcastDelay() {
        return getInteger("broadcast_delay");
    }
}
