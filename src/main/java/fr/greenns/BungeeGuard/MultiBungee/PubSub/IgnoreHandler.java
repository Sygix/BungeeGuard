package fr.greenns.BungeeGuard.MultiBungee.PubSub;

import fr.greenns.BungeeGuard.Main;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Part of fr.greenns.BungeeGuard.MultiBungee.PubSub (bungeeguard)
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
        List<UUID> liste = getListe(joueur);

        switch (action) {
            case "+":
                liste.add(toIgnore);
                break;
            case "-":
                liste.remove(toIgnore);
                if (liste.isEmpty()) {
                    plugin.ignore.remove(joueur);
                }
                break;
        }
    }

    public List<UUID> getListe(UUID joueur) {
        List<UUID> liste;
        if (!plugin.ignore.containsKey(joueur)) {
            liste = new ArrayList<>();
            plugin.ignore.put(joueur, liste);
        } else {
            liste = plugin.ignore.get(joueur);
        }
        return liste;
    }
}
