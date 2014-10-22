package net.uhcwork.BungeeGuard.MultiBungee.PubSub;

import net.uhcwork.BungeeGuard.Main;
import net.uhcwork.BungeeGuard.MultiBungee.PubSubHandler;
import net.uhcwork.BungeeGuard.MultiBungee.PubSubMessageEvent;

import java.util.UUID;

/**
 * Part of net.uhcwork.BungeeGuard.MultiBungee.PubSub (bungeeguard)
 * Date: 10/09/2014
 * Time: 17:57
 * May be open-source & be sold (by mguerreiro, of course !)
 */
public class IgnoreHandler {
    @PubSubHandler("ignore")
    public void ignore(Main plugin, PubSubMessageEvent e) {
        UUID joueur = UUID.fromString(e.getArg(0));
        String action = e.getArg(1);
        UUID toIgnore = UUID.fromString(e.getArg(2));

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
