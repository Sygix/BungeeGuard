package net.uhcwork.BungeeGuard.Models;

import org.javalite.activejdbc.Model;
import org.javalite.activejdbc.annotations.Table;

/**
 * Part of net.uhcwork.BungeeGuard.Models (bungeeguard)
 * Date: 21/09/2014
 * Time: 17:40
 * May be open-source & be sold (by mguerreiro, of course !)
 */
@Table("bungee_premade_message")
public class BungeePremadeMessage extends Model {
    public String getSlug() {
        return getString("slug");
    }

    public String getText() {
        return getString("text");
    }
}
