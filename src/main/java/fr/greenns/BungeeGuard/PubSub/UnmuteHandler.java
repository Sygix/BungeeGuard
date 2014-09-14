package fr.greenns.BungeeGuard.PubSub;

import fr.greenns.BungeeGuard.BungeeGuardUtils;
import fr.greenns.BungeeGuard.Mute.Mute;

import java.util.UUID;

/**
 * Part of fr.greenns.BungeeGuard.PubSub
 * Date: 30/08/2014
 * Time: 15:43
 * May be open-source & be sold (by PunKeel, of course !)
 */
public class UnmuteHandler extends PubSubBase {
    @Override
    public void handle(String channel, String message, String[] args) {
        UUID muteUUID = UUID.fromString(args[0]);
        Mute mute = BungeeGuardUtils.getMute(muteUUID);
        if (mute == null)
            return;
        mute.remove();
    }

    @Override
    public boolean ignoreSelfMessage() {
        return true;
    }

}
