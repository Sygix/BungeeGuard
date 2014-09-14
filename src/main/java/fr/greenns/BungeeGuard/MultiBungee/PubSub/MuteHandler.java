package fr.greenns.BungeeGuard.MultiBungee.PubSub;

import fr.greenns.BungeeGuard.Mute.Mute;

import java.util.UUID;

/**
 * Part of fr.greenns.BungeeGuard.MultiBungee.PubSub
 * Date: 30/08/2014
 * Time: 00:17
 * May be open-source & be sold (by PunKeel, of course !)
 */
public class MuteHandler extends PubSubBase {
    @Override
    public void handle(String channel, String message, String[] args) {
        Mute Mute = new Mute(UUID.fromString(args[1]), args[2], Long.parseLong(args[3]), args[4], args[5], args[6]);
    }

    @Override
    public boolean ignoreSelfMessage() {
        return true;
    }
}
