package net.uhcwork.BungeeGuard.Permissions;

import net.md_5.bungee.api.ChatColor;
import org.javalite.activejdbc.Model;
import org.javalite.activejdbc.annotations.Table;

/**
 * Part of net.uhcwork.BungeeGuard.Permissions (BungeeGuard)
 * Date: 20/10/2014
 * Time: 24:48
 * May be open-source & be sold (by mguerreiro, of course !)
 */
@Table("uhgestion_groups")
public class GroupModel extends Model {
    public String getIdentifier() {
        return getString("id");
    }

    public String getName() {
        return getString("name");
    }

    public String getSuffix() {
        return ChatColor.translateAlternateColorCodes('&', getString("suffix"));
    }

    public String getPrefix() {
        return ChatColor.translateAlternateColorCodes('&', getString("prefix"));
    }

    public String getColor() {
        return ChatColor.translateAlternateColorCodes('&', getString("color"));
    }

    public int getWeight() {
        return getInteger("weight");
    }

    public String getInherit() {
        return getString("inherit");
    }
}
