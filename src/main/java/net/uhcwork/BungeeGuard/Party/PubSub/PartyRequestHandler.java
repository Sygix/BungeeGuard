package net.uhcwork.BungeeGuard.Party.PubSub;

import net.uhcwork.BungeeGuard.Main;
import net.uhcwork.BungeeGuard.MultiBungee.PubSub.PubSubBase;

/**
 * Part of net.uhcwork.BungeeGuard.Party (bungeeguard)
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
        Main.getMB().replyParties(serveur, Main.getGson().toJson(plugin.getPM().getParties()));
    }
}