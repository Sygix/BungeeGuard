package net.uhcwork.BungeeGuard.Permissions;

import net.md_5.bungee.api.ChatColor;
import org.javalite.activejdbc.Model;
import org.javalite.activejdbc.annotations.Table;

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

    public String getChatPrefix() {
        return ChatColor.translateAlternateColorCodes('&', getString("chat_prefix"));
    }

    public String getChatSuffix() {
        return ChatColor.translateAlternateColorCodes('&', getString("chat_suffix"));
    }
}
