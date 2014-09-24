package net.uhcwork.BungeeGuard.MultiBungee.PubSub;

import net.uhcwork.BungeeGuard.Ban.BanManager;
import net.uhcwork.BungeeGuard.Main;
import net.uhcwork.BungeeGuard.Models.BungeeBan;

import java.util.UUID;

/**
 * Part of net.uhcwork.BungeeGuard.MultiBungee.PubSub
 * Date: 30/08/2014
 * Time: 15:50
 * May be open-source & be sold (by PunKeel, of course !)
 */
public class UnBanHandler extends PubSubBase {
    @Override
    public void handle(String channel, String message, String[] args) {
        UUID muteUUID = UUID.fromString(args[1]);
        BanManager BM = Main.plugin.getBM();
        BungeeBan ban = BM.findBan(muteUUID);
        BM.removeBan(ban);
    }

    @Override
    public boolean ignoreSelfMessage() {
        return true;
    }
}
