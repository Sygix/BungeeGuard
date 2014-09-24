package net.uhcwork.BungeeGuard.Models;

import org.javalite.activejdbc.Model;
import org.javalite.activejdbc.annotations.Table;

/**
 * Part of net.uhcwork.BungeeGuard.Models (bungeeguard)
 * Date: 21/09/2014
 * Time: 18:49
 * May be open-source & be sold (by mguerreiro, of course !)
 */
@Table("bungee_blocked_commands")
public class BungeeBlockedCommands extends Model {
    public String getCommand() {
        return getString("command");
    }
}
