package net.uhcwork.BungeeGuard.BanHammer;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.uhcwork.BungeeGuard.Main;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AntiSpamListener implements Listener {
    private final Main plugin;
    private final Map<UUID, String> lastMessage = new HashMap<>();
    private final Map<UUID, Long> lastMessageTime = new HashMap<>();
    private final Map<UUID, Integer> fastMessageCount = new HashMap<>();
    private final Map<UUID, Integer> duplicateCount = new HashMap<>();

    public AntiSpamListener(Main plugin) {
        this.plugin = plugin;
    }

    public void onChat(ChatEvent e) {
        if (!(e.getSender() instanceof ProxiedPlayer))
            return;
        ProxiedPlayer p = (ProxiedPlayer) e.getSender();
        String message = e.getMessage();
        if (antiFlood(p.getUniqueId())) {
            e.setCancelled(true);
            p.sendMessage(TextComponent.fromLegacyText(ChatColor.RED + "Spam détecté."));
            ProxyServer.getInstance().getPluginManager().dispatchCommand(plugin.getProxy().getConsole(), "mute " + p.getName() + " 5m spam");
            return;
        }
        if (antiSpam(p.getUniqueId(), message)) {
            e.setCancelled(true);
            p.sendMessage(TextComponent.fromLegacyText(ChatColor.RED + "Spam détecté."));
        }
        //e.setMessage(noSwear(message));
    }

    private boolean antiFlood(UUID uuid) {
        long now = System.currentTimeMillis();
        long lastMsgMillis = lastMessageTime.containsKey(uuid) ? lastMessageTime.get(uuid) : 0;
        // Anti flood
        if (now - lastMsgMillis < 800) {
            int fastMsgCount = 1 + (fastMessageCount.containsKey(uuid) ? fastMessageCount.get(uuid) : 0);
            fastMessageCount.put(uuid, fastMsgCount);
            if (fastMsgCount >= 2)
                return true;
        } else
            fastMessageCount.put(uuid, 0);
        lastMessageTime.put(uuid, now);
        return false;
    }

    private boolean antiSpam(UUID uuid, String message) {
        /*
         * Si true, bloque le message
         */
        // Anti répétition
        if (lastMessage.containsKey(uuid)
                && LevenshteinDistance.similarity(lastMessage.get(uuid), message) >= 0.6) {
            System.out.println(LevenshteinDistance.similarity(lastMessage.get(uuid), message));
            if (!duplicateCount.containsKey(uuid))
                duplicateCount.put(uuid, 0);
            duplicateCount.put(uuid, duplicateCount.get(uuid) + 1);
        } else {
            duplicateCount.put(uuid, 1);
        }
        lastMessage.put(uuid, message);

        return duplicateCount.get(uuid) > 2;
    }
}

