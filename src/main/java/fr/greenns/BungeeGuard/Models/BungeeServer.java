package fr.greenns.BungeeGuard.Models;

import org.javalite.activejdbc.Model;
import org.javalite.activejdbc.annotations.Table;

/**
 * Part of fr.greenns.BungeeGuard.Models (bungeeguard)
 * Date: 20/09/2014
 * Time: 22:22
 * May be open-source & be sold (by mguerreiro, of course !)
 */

@Table("bungee_servers")
public class BungeeServer extends Model {
    public String getName() {
        return getString("name");
    }

    public String getAddress() {
        return getString("address");
    }
}
