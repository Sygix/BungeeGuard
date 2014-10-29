package net.uhcwork.BungeeGuard.MultiBungee.PubSub;

import net.uhcwork.BungeeGuard.BungeeGuardUtils;
import net.uhcwork.BungeeGuard.Main;
import net.uhcwork.BungeeGuard.Managers.BanManager;
import net.uhcwork.BungeeGuard.Models.BungeeBan;
import net.uhcwork.BungeeGuard.MultiBungee.PubSubHandler;
import net.uhcwork.BungeeGuard.MultiBungee.PubSubMessageEvent;

import java.util.UUID;

/**
 * Part of net.uhcwork.BungeeGuard.Ban
 * Date: 30/08/2014
 * Time: 15:46
 * May be open-source & be sold (by PunKeel, of course !)
 */
public class BanHandler {
    @PubSubHandler("ban")
    public void ban(PubSubMessageEvent e) {
        if (e.getArg(0).equals(BungeeGuardUtils.getServerID()))
            return;
        BanManager BM = Main.plugin.getBanManager();
        BM.ban(UUID.fromString(e.getArg(1)), e.getArg(2), Long.parseLong(e.getArg(3)), e.getArg(4), e.getArg(5),
                UUID.fromString(e.getArg(6)), false);
    }

    @PubSubHandler("unban")
    public void unban(PubSubMessageEvent e) {
        if (e.getArg(0).equals(BungeeGuardUtils.getServerID()))
            return;
        UUID muteUUID = UUID.fromString(e.getArg(1));
        BanManager BM = Main.plugin.getBanManager();
        BungeeBan ban = BM.findBan(muteUUID);
        BM.removeBan(ban);
    }
}
