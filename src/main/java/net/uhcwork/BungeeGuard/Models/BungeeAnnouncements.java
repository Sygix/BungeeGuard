package net.uhcwork.BungeeGuard.Models;

import org.javalite.activejdbc.Model;
import org.javalite.activejdbc.annotations.Table;

/**
 * Part of net.uhcwork.BungeeGuard.Models (bungeeguard)
 * Date: 27/09/2014
 * Time: 16:50
 * May be open-source & be sold (by mguerreiro, of course !)
 */
@Table("bungee_broadcasts")
public class BungeeAnnouncements extends Model {
    public String getText() {
        return getString("message");
    }

    public String getServer() {
        // Liste de serveurs séparée par ":"
        return getString("servers");
    }
}
