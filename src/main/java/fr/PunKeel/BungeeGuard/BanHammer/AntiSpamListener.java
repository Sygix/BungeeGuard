package fr.PunKeel.BungeeGuard.BanHammer;

import fr.PunKeel.BungeeGuard.Main;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.plugin.Listener;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AntiSpamListener implements Listener {
    private final Main plugin;
    private final Map<UUID, String> lastMessage = new HashMap<>();
    private final Map<UUID, Long> lastMessageTime = new HashMap<>();
    private final Map<UUID, Integer> fastMessageCount = new HashMap<>();
    private final Map<UUID, Integer> duplicateCount = new HashMap<>();
    private final Map<UUID, Long> mutes = new HashMap<>();

    public AntiSpamListener(Main plugin) {
        this.plugin = plugin;
    }

    public void onChat(ChatEvent e) {
        if (!(e.getSender() instanceof ProxiedPlayer))
            return;
        ProxiedPlayer p = (ProxiedPlayer) e.getSender();
        String message = e.getMessage();
        if (mutes.containsKey(p.getUniqueId())) {
            if (mutes.get(p.getUniqueId()) > System.currentTimeMillis()) {
                p.sendMessage(TextComponent.fromLegacyText(ChatColor.RED + "Spam détecté."));
                e.setCancelled(true);
                return;
            } else {
                mutes.remove(p.getUniqueId());
            }
        }
        if (antiFlood(p.getUniqueId())) {
            p.sendMessage(TextComponent.fromLegacyText(ChatColor.RED + "Spam détecté. Vous avez été réduit au silence pour une minute."));
            mutes.put(p.getUniqueId(), System.currentTimeMillis() + 1000 * 60 * 1);
            // mute une minute
            e.setCancelled(true);
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
        if (now - lastMsgMillis < 1000) {
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

        long now = System.currentTimeMillis();
        long lastMsgMillis = lastMessageTime.containsKey(uuid) ? lastMessageTime.get(uuid) : 0;

        if (now - lastMsgMillis > 3000)
            return false;

        // Anti répétition
        if (lastMessage.containsKey(uuid)
                && LevenshteinDistance.similarity(lastMessage.get(uuid), message) >= 0.8) {
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

