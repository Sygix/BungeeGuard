package net.uhcwork.BungeeGuard.MultiBungee.PubSub;

import net.uhcwork.BungeeGuard.Main;
import net.uhcwork.BungeeGuard.MultiBungee.PubSubHandler;
import net.uhcwork.BungeeGuard.MultiBungee.PubSubMessageEvent;

import java.util.UUID;

public class PermissionHandler {
    @PubSubHandler("invalidatePermissionUser")
    public static void invalidatePermissionUser(Main plugin, PubSubMessageEvent e) {
        UUID u = UUID.fromString(e.getArg(0));
        plugin.getPermissionManager().invalidateUser(u);
    }
}
