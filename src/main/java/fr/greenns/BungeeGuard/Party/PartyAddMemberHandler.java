package fr.greenns.BungeeGuard.Party;

import fr.greenns.BungeeGuard.Main;
import fr.greenns.BungeeGuard.PubSub.PubSubBase;

import java.util.UUID;

/**
 * Part of fr.greenns.BungeeGuard.Party (bungeeguard)
 * Date: 13/09/2014
 * Time: 16:49
 * May be open-source & be sold (by mguerreiro, of course !)
 */
public class PartyAddMemberHandler extends PubSubBase {
    private final Main plugin;

    public PartyAddMemberHandler(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public void handle(String channel, String message, String[] args) {
        String partyName = args[0];
        UUID u = UUID.fromString(args[1]);
        plugin.getPM().getParty(partyName).addMember(u);
    }
}
