package fr.greenns.BungeeGuard.AntiSpam;

import fr.greenns.BungeeGuard.Main;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.plugin.Listener;

import java.util.HashMap;
import java.util.UUID;

/**
 * Part of fr.greenns.BungeeGuard.AntiSpam (bungeeguard)
 * Date: 21/09/2014
 * Time: 18:42
 * May be open-source & be sold (by mguerreiro, of course !)
 */
public class AntiSpamListener implements Listener {
    private final Main plugin;
    public HashMap<UUID, String> lastMessage = new HashMap<>();
    public HashMap<UUID, Integer> duplicateCount = new HashMap<>();

    public AntiSpamListener(Main plugin) {
        this.plugin = plugin;
    }

    public void onChat(ChatEvent e) {
        if (!(e.getSender() instanceof ProxiedPlayer))
            return;
        ProxiedPlayer p = (ProxiedPlayer) e.getSender();
        if (lastMessage.containsKey(p.getUniqueId()) && lastMessage.get(p.getUniqueId()).equalsIgnoreCase(e.getMessage())) {
            if (!duplicateCount.containsKey(p.getUniqueId()))
                duplicateCount.put(p.getUniqueId(), 0);
            duplicateCount.put(p.getUniqueId(), duplicateCount.get(p.getUniqueId()) + 1);
        } else {
            duplicateCount.put(p.getUniqueId(), 1);
        }
        lastMessage.put(p.getUniqueId(), e.getMessage());

        if (duplicateCount.get(p.getUniqueId()) > 2) {
            e.setCancelled(true);
            e.setMessage("");
        }
    }
}
