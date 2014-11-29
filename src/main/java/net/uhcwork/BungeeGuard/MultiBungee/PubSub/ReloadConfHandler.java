package net.uhcwork.BungeeGuard.MultiBungee.PubSub;

import gnu.trove.map.TMap;
import net.md_5.bungee.api.ProxyConfig;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.util.CaseInsensitiveMap;
import net.uhcwork.BungeeGuard.Main;
import net.uhcwork.BungeeGuard.MultiBungee.PubSubHandler;
import net.uhcwork.BungeeGuard.MultiBungee.PubSubMessageEvent;

import java.net.InetSocketAddress;
import java.util.Iterator;
import java.util.Map;

/**
 * Part of ${PACKAGE_NAME} (${PROJECT_NAME})
 * Date: 07/09/2014
 * Time: 18:22
 * May be open-source & be sold (by mguerreiro, of course !)
 */
public class ReloadConfHandler {
    @PubSubHandler("reloadConf")
    public static void reloadConf(Main plugin, PubSubMessageEvent e) {
        plugin.getLogger().info("Reloading groups & permissions");
        plugin.getPermissionManager().loadGroups();
        if (plugin.getRandom().nextInt(1000) == 5) {
            plugin.getPermissionManager().loadUsers();
        }

        plugin.getLogger().info("Saving current configuration");
        @SuppressWarnings("deprecation")
        ProxyConfig oldConfig = plugin.getProxy().getConfig();
        TMap<String, ServerInfo> oldServerInfos = new CaseInsensitiveMap<>();
        oldServerInfos.putAll(oldConfig.getServers());

        plugin.getLogger().info("Checking the new configuration");
        plugin.getProxy().getConfig().getServers().clear();
        try {
            @SuppressWarnings("deprecation")
            ProxyConfig c = ProxyServer.getInstance().getConfig();
            c.getClass().getMethod("load").invoke(c);
        } catch (Throwable t) {
            t.printStackTrace();
            plugin.getLogger().info("Failed to reload this config, restoring old servers map, please fix it before reloading again");
            ProxyServer.getInstance().getConfig().getServers().putAll(oldServerInfos);
            return;
        }

        @SuppressWarnings("deprecation")
        ProxyConfig newConfig = ProxyServer.getInstance().getConfig();
        Map<String, ServerInfo> newServerInfos = newConfig.getServers();

        plugin.getLogger().info("Loading the new configuration");

        Iterator<Map.Entry<String, ServerInfo>> it = newServerInfos.entrySet().iterator();
        String serverName;
        while (it.hasNext()) {
            Map.Entry<String, ServerInfo> newServer = it.next();
            serverName = newServer.getKey();

            if (oldServerInfos.containsKey(serverName)) {
                ServerInfo oldServerInfo = oldServerInfos.get(serverName);
                InetSocketAddress oldIp = oldServerInfo.getAddress();
                String oldMotd = oldServerInfo.getMotd();

                ServerInfo newServerInfo = newServer.getValue();
                InetSocketAddress newIp = newServerInfo.getAddress();
                String newMotd = newServerInfo.getMotd();

                if ((oldIp.equals(newIp)) && (oldMotd.equals(newMotd))) {
                    newServerInfos.put(serverName, oldServerInfo);
                } else {
                    plugin.getLogger().info(serverName + " has been edited, ProxiedPlayers count will not be restored for this server !");
                }
                oldServerInfos.remove(serverName);
            } else {
                plugin.getLogger().info(serverName + " added");
            }
        }

        for (ServerInfo si : oldServerInfos.values()) {
            plugin.getLogger().info(si.getName() + " removed");
        }
        plugin.getLogger().info("-Configuration reloaded !-");


        plugin.getLogger().info("Reloading bans/mutes");
        plugin.getSanctionManager().loadBans();
        plugin.getSanctionManager().loadMutes();

    }

    public void handle(Main plugin) {
        reloadConf(plugin, null);
    }
}
