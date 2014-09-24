package net.uhcwork.BungeeGuard.MultiBungee.PubSub;

import net.uhcwork.BungeeGuard.Main;
import net.uhcwork.BungeeGuard.Models.BungeeMute;
import net.uhcwork.BungeeGuard.Mute.MuteManager;

import java.util.UUID;

/**
 * Part of net.uhcwork.BungeeGuard.MultiBungee.PubSub
 * Date: 30/08/2014
 * Time: 15:43
 * May be open-source & be sold (by PunKeel, of course !)
 */
public class UnmuteHandler extends PubSubBase {
    @Override
    public void handle(String channel, String message, String[] args) {
        UUID muteUUID = UUID.fromString(args[1]);
        MuteManager MM = Main.plugin.getMM();
        BungeeMute mute = MM.findMute(muteUUID);
        MM.removeMute(mute);
    }

    @Override
    public boolean ignoreSelfMessage() {
        return true;
    }

}
