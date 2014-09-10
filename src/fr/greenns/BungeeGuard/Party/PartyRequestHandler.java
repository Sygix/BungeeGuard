package fr.greenns.BungeeGuard.Party;

import fr.greenns.BungeeGuard.Main;
import fr.greenns.BungeeGuard.PubSub.PubSubBase;

/**
 * Part of fr.greenns.BungeeGuard.Party (bungeeguard)
 * Date: 10/09/2014
 * Time: 18:22
 * May be open-source & be sold (by mguerreiro, of course !)
 */
public class PartyRequestHandler extends PubSubBase {
    private final Main plugin;

    public PartyRequestHandler(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public void handle(String channel, String message, String[] args) {
        String serveur = args[0];
        plugin.getMB().replyParties(serveur, plugin.gson.toJson(plugin.getPM().getParties()));
    }
}