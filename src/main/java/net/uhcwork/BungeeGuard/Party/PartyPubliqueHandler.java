package net.uhcwork.BungeeGuard.Party;

import net.uhcwork.BungeeGuard.Main;
import net.uhcwork.BungeeGuard.MultiBungee.PubSub.PubSubBase;

/**
 * Part of net.uhcwork.BungeeGuard.Party (bungeeguard)
 * Date: 10/09/2014
 * Time: 21:23
 * May be open-source & be sold (by mguerreiro, of course !)
 */
public class PartyPubliqueHandler extends PubSubBase {
    Main plugin;

    public PartyPubliqueHandler(Main plugin) {
        {
            this.plugin = plugin;
        }
    }

    @Override
    public void handle(String channel, String message, String[] args) {
        String partyName = args[0];
        boolean isPublique = Boolean.parseBoolean(args[1]);
        plugin.getPM().getParty(partyName).setPublique(isPublique);
    }
}