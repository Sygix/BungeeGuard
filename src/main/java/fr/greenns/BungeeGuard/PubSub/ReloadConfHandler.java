package fr.greenns.BungeeGuard.PubSub;

import fr.greenns.BungeeGuard.Main;
import gnu.trove.map.TMap;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.conf.Configuration;
import net.md_5.bungee.util.CaseInsensitiveMap;

import java.net.InetSocketAddress;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by mguerreiro on 07/09/2014.
 */
public class ReloadConfHandler extends PubSubBase {
    Main plugin;

    public ReloadConfHandler(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public void handle(String channel, String message, String[] args) {
        plugin.getLogger().info("Saving current configuration");
        Configuration oldConfig = BungeeCord.getInstance().config;
        TMap<String, ServerInfo> oldServerInfos = new CaseInsensitiveMap<>();
        oldServerInfos.putAll(oldConfig.getServers());

        plugin.getLogger().info("Checking the new configuration");
        BungeeCord.getInstance().config.getServers().clear();
        try {
            BungeeCord.getInstance().config.load();
        } catch (Throwable t) {
            t.printStackTrace();
            plugin.getLogger().info("Failed to reload this config, restoring old servers map, please fix it before reloading again");
            BungeeCord.getInstance().config.getServers().putAll(oldServerInfos);
            return;
        }

        Configuration newConfig = BungeeCord.getInstance().config;
        TMap<String, ServerInfo> newServerInfos = newConfig.getServers();

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
    }

    public void handle() {
        handle(null, null, null);
    }
}
