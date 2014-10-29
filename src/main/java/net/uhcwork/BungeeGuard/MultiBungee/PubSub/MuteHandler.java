package net.uhcwork.BungeeGuard.MultiBungee.PubSub;

import net.uhcwork.BungeeGuard.BungeeGuardUtils;
import net.uhcwork.BungeeGuard.Main;
import net.uhcwork.BungeeGuard.Managers.MuteManager;
import net.uhcwork.BungeeGuard.Models.BungeeMute;
import net.uhcwork.BungeeGuard.MultiBungee.PubSubHandler;
import net.uhcwork.BungeeGuard.MultiBungee.PubSubMessageEvent;

import java.util.UUID;

/**
 * Part of net.uhcwork.BungeeGuard.MultiBungee.PubSub
 * Date: 30/08/2014
 * Time: 00:17
 * May be open-source & be sold (by PunKeel, of course !)
 */
public class MuteHandler {
    @PubSubHandler("mute")
    public void mute(Main plugin, PubSubMessageEvent e) {
        if (e.getArg(0).equals(BungeeGuardUtils.getServerID()))
            return;
        plugin.getMuteManager().mute(UUID.fromString(e.getArg(1)), e.getArg(2), Long.parseLong(e.getArg(3)), e.getArg(4),
                e.getArg(5), UUID.fromString(e.getArg(6)), false);

    }

    @PubSubHandler("unmute")
    public void handle(Main plugin, PubSubMessageEvent e) {
        if (e.getArg(0).equals(BungeeGuardUtils.getServerID()))
            return;
        UUID muteUUID = UUID.fromString(e.getArg(1));
        MuteManager MM = plugin.getMuteManager();
        BungeeMute mute = MM.findMute(muteUUID);
        MM.removeMute(mute);
    }


}
