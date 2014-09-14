package fr.greenns.BungeeGuard.Party;

import fr.greenns.BungeeGuard.Main;
import fr.greenns.BungeeGuard.PubSub.PubSubBase;

import java.util.UUID;

/**
 * Part of fr.greenns.BungeeGuard.Party (bungeeguard)
 * Date: 13/09/2014
 * Time: 16:25
 * May be open-source & be sold (by mguerreiro, of course !)
 */

public class PartyCreateHandler extends PubSubBase {
    Main plugin;

    public PartyCreateHandler(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public void handle(String channel, String message, String[] args) {
        // partyName(), "" + joueur
        String partyName = args[0];
        UUID u = UUID.fromString(args[1]);
        plugin.getPM().createParty(partyName, u);
    }
}
