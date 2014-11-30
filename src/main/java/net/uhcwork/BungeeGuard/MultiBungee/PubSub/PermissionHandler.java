package net.uhcwork.BungeeGuard.MultiBungee.PubSub;

import net.uhcwork.BungeeGuard.Main;
import net.uhcwork.BungeeGuard.MultiBungee.PubSubHandler;
import net.uhcwork.BungeeGuard.MultiBungee.PubSubMessageEvent;

import java.util.UUID;

/**
 * Part of net.uhcwork.BungeeGuard.MultiBungee.PubSub (BungeeGuard)
 * Date: 22/10/2014
 * Time: 23:05
 * May be open-source & be sold (by mguerreiro, of course !)
 */
public class PermissionHandler {
    @PubSubHandler("invalidatePermissionUser")
    public void invalidatePermissionUser(Main plugin, PubSubMessageEvent e) {
        UUID u = UUID.fromString(e.getArg(0));
        plugin.getPermissionManager().invalidateUser(u);
    }
}
