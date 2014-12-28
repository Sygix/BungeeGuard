package net.uhcwork.BungeeGuard.Config;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import lombok.Getter;
import net.md_5.bungee.Util;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ConfigurationAdapter;
import net.md_5.bungee.api.config.ListenerInfo;
import net.md_5.bungee.api.config.ServerInfo;
import net.uhcwork.BungeeGuard.BungeeGuardUtils;
import net.uhcwork.BungeeGuard.Main;
import net.uhcwork.BungeeGuard.Models.*;
import net.uhcwork.BungeeGuard.Persistence.VoidRunner;

import java.net.InetSocketAddress;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class MysqlConfigAdapter implements ConfigurationAdapter {
    private final Main plugin;
    private final HashMap<String, ServerInfo> servers = new HashMap<>();
    @Getter
    String motd;
    private String host;
    private ListenerInfo listener = null;
    private Map<String, String> forced_hosts;
    private BungeeConfig options;
    private Map<String, BungeeServer> _servers = new HashMap<>();
    @Getter
    private String[] welcomeSubtitles = {
            ChatColor.GOLD + "Des pommes, " + ChatColor.WHITE + "du hardcore, " + ChatColor.RED + "du fun !",
            ChatColor.AQUA + "Le meilleur de l'" + ChatColor.RED + "UltraHardCore " + ChatColor.AQUA + "!",
            ChatColor.AQUA + "La " + ChatColor.GOLD + "pomme d'or" + ChatColor.AQUA + " est votre seule alliée !",
            ChatColor.AQUA + "Le serveur totalement en " + ChatColor.RED + "UltraHardCore " + ChatColor.AQUA + "!"};

    public MysqlConfigAdapter(Main plugin) {
        this.plugin = plugin;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void load() {
        Future<Void> x = plugin.executePersistenceRunnable(new VoidRunner() {
            @Override
            protected void run() {
                loadConf();
                loadListener();
                loadServers();
                loadForcedHosts();
                loadWelcomeTitles();
            }
        });
        try {
            x.get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            System.out.println("[Conf:MYSQL] Aborted. " + e.getMessage());
        }
    }

    @Override
    public int getInt(String path, int def) {
        switch (path) {
            case "timeout":
                return 900000;
            case "player_limit":
            case "connection_throttle":
                return -1;
            default:
                return def;
        }
    }

    @Override
    public String getString(String path, String def) {
        switch (path) {
            case "stats":
                return "0-0-0-0";
            default:
                return def;
        }
    }

    @Override
    public boolean getBoolean(String path, boolean def) {
        switch (path) {
            case "online_mode":
            case "ip_forward":
                return true;
            default:
                return def;
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public Map<String, ServerInfo> getServers() {
        return servers;
    }

    void setServers(HashMap<String, ServerInfo> new_servers, HashMap<String, BungeeServer> _new_servers) {
        servers.clear();
        servers.putAll(new_servers);

        _servers.clear();
        _servers.putAll(_new_servers);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Collection<ListenerInfo> getListeners() {
        List<ListenerInfo> x = new ArrayList<>();
        x.add(listener);
        return x;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Collection<String> getGroups(String player) {
        Collection<String> ret = new HashSet<>();
        ret.add("default");
        return ret;
    }

    @Override
    public Collection<?> getList(String path, Collection<?> def) {
        switch (path) {
            case "disabled_commands":
            default:
                return def;
        }
    }

    @Override
    public Collection<String> getPermissions(String group) {
        return Collections.emptySet();
    }

    public Map<String, String> getForcedHosts() {
        return forced_hosts;
    }

    public int getMaxPlayers() {
        return options.getMaxPlayers();
    }


    void loadConf() {
        options = BungeeConfig.findById(1);
        motd = options.getMotd();
        BungeeInstance instance = BungeeInstance.findFirst("server_id = ?", BungeeGuardUtils.getServerID());

        host = instance.getBindAddress();

        List<BungeePremadeMessage> premadeMessages = BungeePremadeMessage.findAll();
        plugin.getSanctionManager().setPremadeMessages(premadeMessages);

        List<BungeeBlockedCommands> blockedCommands = BungeeBlockedCommands.findAll();
        plugin.setForbiddenCommands(blockedCommands);

        List<BungeeAnnouncements> announcements = BungeeAnnouncements.findAll();
        plugin.getAnnouncementManager().setAnnouncements(announcements);
        plugin.getAnnouncementManager().setBroadcastDelay(options.getBroadcastDelay());
    }

    void loadServers() {
        List<BungeeServer> _s = BungeeServer.findAll();
        HashMap<String, ServerInfo> servers_new = new HashMap<>();
        HashMap<String, BungeeServer> _servers_new = new HashMap<>();
        for (BungeeServer serveur : _s) {
            String name = serveur.getName();
            String addr = serveur.getAddress();

            _servers_new.put(name, serveur);

            InetSocketAddress address = Util.getAddr(addr);
            ServerInfo info = ProxyServer.getInstance().constructServerInfo(name, address, "", true);
            servers_new.put(name, info);
        }
        setServers(servers_new, _servers_new);
    }

    void loadListener() {
        if (listener != null)
            return;
        // Bungee n'apprécie pas trop qu'on change d'ip:port d'écoute quand il tourne, donc on attend reboot :)

        Map<String, String> forced = new HashMap<>();
        String defaultServer = "hub";
        String fallbackServer = "hub";
        boolean forceDefault = true;
        int tabListSize = 60;
        boolean setLocalAddress = false; // On laisse debian s'en occuper :}
        boolean pingPassthrough = false;
        boolean query = false;
        int queryPort = 25577;
        String motd = "";
        int maxPlayers = 500;
        InetSocketAddress address = Util.getAddr(host);
        //noinspection ConstantConditions
        listener = new ListenerInfo(address, motd, maxPlayers, tabListSize, defaultServer, fallbackServer,
                forceDefault, forced, "SERVER", setLocalAddress, pingPassthrough, queryPort, query);

    }

    void loadForcedHosts() {
        Map<String, String> forced_hosts2 = new HashMap<>();
        List<BungeeForcedHost> bfh = BungeeForcedHost.findAll();
        for (BungeeForcedHost bf : bfh) {
            forced_hosts2.put(bf.getIp().toLowerCase(), bf.getServer());
        }
        forced_hosts = forced_hosts2;
    }

    void loadWelcomeTitles() {
        List<BungeeWelcomeTitle> subtitles = BungeeWelcomeTitle.findAll().load();
        welcomeSubtitles = Collections2.transform(subtitles, new Function<BungeeWelcomeTitle, String>() {
            @Override
            public String apply(BungeeWelcomeTitle bungeeWelcomeTitle) {
                return bungeeWelcomeTitle.getMessage();
            }
        }).toArray(new String[]{""});
    }

    public BungeeServer getServer(String serverName) {
        return _servers.get(serverName);
    }
}