package net.uhcwork.BungeeGuard.Models;

import net.md_5.bungee.api.ChatColor;
import org.javalite.activejdbc.Model;
import org.javalite.activejdbc.annotations.Table;

@Table("bungee_welcome_title")
public class BungeeWelcomeTitle extends Model {
    public String getMessage() {
        return ChatColor.translateAlternateColorCodes('&', getString("message"));
    }
}
