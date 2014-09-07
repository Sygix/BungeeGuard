package fr.greenns.BungeeGuard.utils;

import net.md_5.bungee.BungeeCord;

/**
 * Part of fr.greenns.BungeeGuard.utils
 * Date: 30/08/2014
 * Time: 14:16
 * May be open-source & be sold (by PunKeel, of course !)
 */
public class Permissions {
    // Works even with offline players !
    public static boolean hasPerm(String player, String permission) {
        for (String group : BungeeCord.getInstance().getConfigurationAdapter().getGroups(player)) {
            for (String p : BungeeCord.getInstance().getConfigurationAdapter().getPermissions(group)) {
                if (permission.equalsIgnoreCase(p) || permission.equalsIgnoreCase("*"))
                    return true;
            }
        }
        return false;
    }
}
