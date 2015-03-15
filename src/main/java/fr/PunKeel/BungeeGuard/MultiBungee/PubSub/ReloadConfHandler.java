package fr.PunKeel.BungeeGuard.MultiBungee.PubSub;

import fr.PunKeel.BungeeGuard.Config.MysqlConfigAdapter;
import fr.PunKeel.BungeeGuard.Main;
import fr.PunKeel.BungeeGuard.MultiBungee.PubSubHandler;
import fr.PunKeel.BungeeGuard.MultiBungee.PubSubMessageEvent;
import gnu.trove.map.TMap;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.util.CaseInsensitiveMap;

import java.net.InetSocketAddress;
import java.util.Iterator;
import java.util.Map;

public class ReloadConfHandler {
    @PubSubHandler("reloadConf")
    public static void reloadConf(Main plugin, PubSubMessageEvent e) {
        plugin.getLogger().info("Reloading groups & permissions");
        plugin.getPermissionManager().loadGroups();
        if (Main.getRandom().nextInt(1000) == 5) {
            plugin.getPermissionManager().loadUsers();
        }
        plugin.getLogger().info("Saving current configuration");
        MysqlConfigAdapter config = plugin.getConfig();
        TMap<String, ServerInfo> oldServerInfos = new CaseInsensitiveMap<>(config.getServers());

        plugin.getLogger().info("Checking the new configuration");

        config.load();

        Iterator<Map.Entry<String, ServerInfo>> it = config.getServers().entrySet().iterator();
        String serverName;
        while (it.hasNext()) {
            Map.Entry<String, ServerInfo> newServer = it.next();
            serverName = newServer.getKey();

            if (oldServerInfos.containsKey(serverName)) {
                ServerInfo oldServerInfo = oldServerInfos.get(serverName);
                InetSocketAddress oldIp = oldServerInfo.getAddress();
                ServerInfo newServerInfo = newServer.getValue();
                InetSocketAddress newIp = newServerInfo.getAddress();

                if (oldIp.equals(newIp)) {
                    config.getServers().put(serverName, oldServerInfo);
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
