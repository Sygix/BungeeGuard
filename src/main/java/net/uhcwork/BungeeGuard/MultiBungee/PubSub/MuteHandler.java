package net.uhcwork.BungeeGuard.MultiBungee.PubSub;

import net.uhcwork.BungeeGuard.Main;

import java.util.UUID;

/**
 * Part of net.uhcwork.BungeeGuard.MultiBungee.PubSub
 * Date: 30/08/2014
 * Time: 00:17
 * May be open-source & be sold (by PunKeel, of course !)
 */
public class MuteHandler extends PubSubBase {
    @Override
    public void handle(String channel, String message, String[] args) {
        Main.plugin.getMM().mute(UUID.fromString(args[1]), args[2], Long.parseLong(args[3]), args[4],
                args[5], UUID.fromString(args[6]), false);

    }

    @Override
    public boolean ignoreSelfMessage() {
        return true;
    }
}
