package fr.greenns.BungeeGuard.Models;

import org.javalite.activejdbc.Model;
import org.javalite.activejdbc.annotations.Table;

/**
 * Part of fr.greenns.BungeeGuard.Models (bungeeguard)
 * Date: 20/09/2014
 * Time: 22:23
 * May be open-source & be sold (by mguerreiro, of course !)
 */
@Table("bungee_forced_host")
public class BungeeForcedHost extends Model {

    public String getIp() {
        return getString("ip");
    }

    public String getServer() {
        return getString("to_server");
    }
}
