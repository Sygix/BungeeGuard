package net.uhcwork.BungeeGuard.Models;

import org.javalite.activejdbc.Model;
import org.javalite.activejdbc.annotations.Table;

/**
 * Part of net.uhcwork.BungeeGuard.Models (bungeeguard)
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

    public String getPrettyName() {
        return getString("pretty_name");
    }
}
