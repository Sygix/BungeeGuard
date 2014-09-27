package net.uhcwork.BungeeGuard.Announces;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.chat.ComponentSerializer;
import net.uhcwork.BungeeGuard.Main;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Part of net.uhcwork.BungeeGuard (bungeeguard)
 * Date: 27/09/2014
 * Time: 16:38
 * May be open-source & be sold (by mguerreiro, of course !)
 */
public class AnnouncementTask implements Runnable {
    AnnouncementManager AM;
    Main plugin;
    private Map<String, Integer> index = Maps.newHashMap();
    private LoadingCache<String, Pattern> regexCache = CacheBuilder.newBuilder().build(new CacheLoader<String, Pattern>() {
        @Override
        public Pattern load(String s) throws Exception {
            return Pattern.compile(s);
        }
    });
    private int timeSinceLastRun = 0;

    @Override
    public void run() {
        if (plugin == null) {
            plugin = Main.plugin;
            AM = plugin.getAM();
        }
        if (timeSinceLastRun + 1 >= plugin.getBroadcastDelay()) {
            timeSinceLastRun = 0;
        } else {
            timeSinceLastRun++;
            return;
        }

        String prefix = ChatColor.AQUA + "[" + ChatColor.GOLD + "***" + ChatColor.AQUA + "] " + ChatColor.RESET;

        // Select and display our announcements.
        for (Map.Entry<String, ServerInfo> entry : ProxyServer.getInstance().getServers().entrySet()) {
            if (entry.getValue().getPlayers().isEmpty())
                continue;

            if (!index.containsKey(entry.getKey()))
                index.put(entry.getKey(), 0);

            Announcement announcement = selectAnnouncementFor(entry.getKey());

            if (announcement == null)
                continue;

            List<BaseComponent[]> components = new ArrayList<>();

            String line = announcement.getText();
            if (line.startsWith("{")) {
                try {
                    BaseComponent[] components2 = ComponentSerializer.parse(line);
                    BaseComponent[] prefixComp = TextComponent.fromLegacyText(prefix);

                    if (prefixComp.length != 0)
                        prefixComp[prefixComp.length - 1].setExtra(Arrays.asList(components2));
                    else
                        prefixComp = components2;

                    components.add(prefixComp);
                } catch (Exception ignored) {
                    components.add(TextComponent.fromLegacyText(prefix + ChatColor.translateAlternateColorCodes('&', line)));
                }
            } else {
                components.add(TextComponent.fromLegacyText(prefix + ChatColor.translateAlternateColorCodes('&', line)));
            }


            for (ProxiedPlayer player : entry.getValue().getPlayers()) {
                for (BaseComponent[] component : components) {
                    player.sendMessage(component);
                }
            }
        }
    }

    private Announcement selectAnnouncementFor(String server) {
        List<Announcement> announcements = ImmutableList.copyOf(plugin.getAM().getAnnouncements());
        Announcement a;
        int tries = 0;
        while (tries < 5) {
            a = announcements.get(index.get(server));
            advanced(server);
            if (doesAnnouncementMatch(a, server))
                return a;
            tries++;
        }
        return null;
    }

    private void advanced(String key) {
        int val = index.get(key);

        if (val + 1 == AM.getAnnouncements().size())
            index.put(key, 0);
        else
            index.put(key, val + 1);
    }

    private List<Pattern> producePatterns(List<String> patterns) {
        List<Pattern> patterns1 = new ArrayList<>();
        for (String pattern : patterns) {
            patterns1.add(regexCache.getUnchecked(pattern));
        }
        return patterns1;
    }

    private boolean doesAnnouncementMatch(Announcement announcement, String server) {
        if (announcement.getServers().contains(server) || announcement.getServers().contains("global")) {
            return true;
        }
        for (Pattern pattern : producePatterns(announcement.getServers())) {
            if (pattern.matcher(server).find()) {
                return true;
            }
        }
        return false;
    }
}