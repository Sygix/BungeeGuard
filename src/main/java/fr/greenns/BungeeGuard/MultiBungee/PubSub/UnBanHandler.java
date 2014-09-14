package fr.greenns.BungeeGuard.MultiBungee.PubSub;

import fr.greenns.BungeeGuard.Ban.Ban;
import fr.greenns.BungeeGuard.BungeeGuardUtils;

import java.util.UUID;

/**
 * Part of fr.greenns.BungeeGuard.MultiBungee.PubSub
 * Date: 30/08/2014
 * Time: 15:50
 * May be open-source & be sold (by PunKeel, of course !)
 */
public class UnBanHandler extends PubSubBase {
    @Override
    public void handle(String channel, String message, String[] args) {
        UUID muteUUID = UUID.fromString(args[1]);
        Ban ban = BungeeGuardUtils.getBan(muteUUID);
        if (ban == null)
            return;
        ban.remove();
    }

    @Override
    public boolean ignoreSelfMessage() {
        return true;
    }
}
