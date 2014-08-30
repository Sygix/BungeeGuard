package fr.greenns.BungeeGuard.PubSub;

import fr.greenns.BungeeGuard.BungeeGuardUtils;
import fr.greenns.BungeeGuard.Mute.Mute;
import net.md_5.bungee.Util;

/**
 * Part of fr.greenns.BungeeGuard.PubSub
 * Date: 30/08/2014
 * Time: 00:17
 * May be open-source & be sold (by PunKeel, of course !)
 */
public class MuteHandler implements PubSubBase {
    @Override
    public void handle(String channel, String message, String[] args) {
        if (args[0].equalsIgnoreCase(BungeeGuardUtils.getMB().getServerId()))
            return;
        Mute Mute = new Mute(Util.getUUID(args[1]), args[2], Long.parseLong(args[3]), args[4], args[5], args[6]);
    }
}
