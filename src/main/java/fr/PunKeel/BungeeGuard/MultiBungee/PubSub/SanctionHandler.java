package fr.PunKeel.BungeeGuard.MultiBungee.PubSub;

import fr.PunKeel.BungeeGuard.Main;
import fr.PunKeel.BungeeGuard.Managers.SanctionManager;
import fr.PunKeel.BungeeGuard.Models.BungeeBan;
import fr.PunKeel.BungeeGuard.Models.BungeeMute;
import fr.PunKeel.BungeeGuard.MultiBungee.PubSubHandler;
import fr.PunKeel.BungeeGuard.MultiBungee.PubSubMessageEvent;

import java.util.UUID;

public class SanctionHandler {
    @PubSubHandler("mute")
    public static void mute(Main plugin, PubSubMessageEvent e) {
        if (e.getArg(0).equals(Main.getMB().getServerId()))
            return;
        plugin.getSanctionManager().mute(UUID.fromString(e.getArg(1)), e.getArg(2), Long.parseLong(e.getArg(3)), e.getArg(4),
                e.getArg(5), UUID.fromString(e.getArg(6)), false);

    }

    @PubSubHandler("unmute")
    public static void handle(Main plugin, PubSubMessageEvent e) {
        if (e.getArg(0).equals(Main.getMB().getServerId()))
            return;
        UUID muteUUID = UUID.fromString(e.getArg(1));
        SanctionManager MM = plugin.getSanctionManager();
        BungeeMute mute = MM.findMute(muteUUID);
        MM.removeMute(mute);
    }

    /**
     * @param e A redis pub sub event
     */
    @PubSubHandler("ban")
    public static void ban(PubSubMessageEvent e) {
        if (e.getArg(0).equals(Main.getMB().getServerId()))
            return;
        SanctionManager BM = Main.plugin.getSanctionManager();
        BM.ban(UUID.fromString(e.getArg(1)), e.getArg(2), Long.parseLong(e.getArg(3)), e.getArg(4), e.getArg(5),
                UUID.fromString(e.getArg(6)), false);
    }

    /**
     * @param event A redis pub sub event
     */
    @PubSubHandler("unban")
    public static void unban(PubSubMessageEvent event) {
        if (event.getArg(0).equals(Main.getMB().getServerId()))
            return;
        UUID muteUUID = UUID.fromString(event.getArg(1));
        SanctionManager BM = Main.plugin.getSanctionManager();
        BungeeBan ban = BM.findBan(muteUUID);
        BM.removeBan(ban);
    }

}
