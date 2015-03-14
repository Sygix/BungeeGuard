package fr.PunKeel.BungeeGuard.MultiBungee.PubSub;

import fr.PunKeel.BungeeGuard.Main;
import fr.PunKeel.BungeeGuard.MultiBungee.PubSubHandler;
import fr.PunKeel.BungeeGuard.MultiBungee.PubSubMessageEvent;

import java.util.UUID;

public class PermissionHandler {
    @PubSubHandler("invalidatePermissionUser")
    public static void invalidatePermissionUser(Main plugin, PubSubMessageEvent e) {
        UUID u = UUID.fromString(e.getArg(0));
        plugin.getPermissionManager().invalidateUser(u);
    }
}
