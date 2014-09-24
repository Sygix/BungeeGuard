package net.uhcwork.BungeeGuard.MultiBungee.PubSub;

import net.uhcwork.BungeeGuard.Main;

import java.util.UUID;

/**
 * Part of net.uhcwork.BungeeGuard.MultiBungee.PubSub (bungeeguard)
 * Date: 10/09/2014
 * Time: 17:57
 * May be open-source & be sold (by mguerreiro, of course !)
 */
public class IgnoreHandler extends PubSubBase {
    Main plugin;

    public IgnoreHandler(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public void handle(String channel, String message, String[] args) {
        UUID joueur = UUID.fromString(args[0]);
        String action = args[0];
        UUID toIgnore = UUID.fromString(args[2]);

        switch (action) {
            case "+":
                plugin.getIM().ignore(joueur, toIgnore);
                break;
            case "-":
                plugin.getIM().unIgnore(joueur, toIgnore);
                break;
        }
    }
}
