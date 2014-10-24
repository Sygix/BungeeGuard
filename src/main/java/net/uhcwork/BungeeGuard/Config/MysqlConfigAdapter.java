package net.uhcwork.BungeeGuard.Config;

import net.md_5.bungee.Util;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ConfigurationAdapter;
import net.md_5.bungee.api.config.ListenerInfo;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.util.CaseInsensitiveMap;
import net.uhcwork.BungeeGuard.BungeeGuardUtils;
import net.uhcwork.BungeeGuard.Main;
import net.uhcwork.BungeeGuard.Models.*;
import net.uhcwork.BungeeGuard.Persistence.VoidRunner;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.net.InetSocketAddress;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class MysqlConfigAdapter implements ConfigurationAdapter {
    private final Yaml yaml;
    private Map<String, Object> config;
    private Main plugin;
    private String host;
    private HashMap<String, ServerInfo> servers = new HashMap<>();
    private ListenerInfo listener = null;
    private Map<String, String> forced_hosts;
    private Map<OPTIONS, Object> options;
    private int maxPlayers;


    public MysqlConfigAdapter(Main plugin) {
        this.plugin = plugin;
        DumperOptions options = new DumperOptions();
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        yaml = new Yaml(options);
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

    private <T> T get(String path, T def) {
        return get(path, def, config);
    }

    @SuppressWarnings("unchecked")
    private <T> T get(String path, T def, Map submap) {
        int index = path.indexOf('.');
        if (index == -1) {
            Object val = submap.get(path);
            if (val == null && def != null) {
                val = def;
                submap.put(path, def);
                save();
            }
            return (T) val;
        } else {
            String first = path.substring(0, index);
            String second = path.substring(index + 1, path.length());
            Map sub = (Map) submap.get(first);
            if (sub == null) {
                sub = new LinkedHashMap();
                submap.put(first, sub);
            }
            return get(second, def, sub);
        }
    }

    private void save() {
    }

    @Override
    public int getInt(String path, int def) {
        return get(path, def);
    }

    @Override
    public String getString(String path, String def) {
        return get(path, def);
    }

    @Override
    public boolean getBoolean(String path, boolean def) {
        return get(path, def);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Map<String, ServerInfo> getServers() {
        return servers;
    }

    public void setServers(HashMap<String, ServerInfo> _servers) {
        servers.clear();
        servers.putAll(_servers);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Collection<ListenerInfo> getListeners() {
        List<ListenerInfo> x = new ArrayList<>();
        x.add(listener);
        return x;
    }

    private Map<OPTIONS, Object> getOptions() {
        return options;
    }


    @Override
    @SuppressWarnings("unchecked")
    public Collection<String> getGroups(String player) {
        Collection<String> ret = new HashSet<String>();
        ret.add("default");
        return ret;
    }

    @Override
    public Collection<?> getList(String path, Collection<?> def) {
        return get(path, def);
    }

    @Override
    public Collection<String> getPermissions(String group) {
        return Collections.emptySet();
    }

    public Map<String, String> getForcedHosts() {
        return forced_hosts;
    }

    public int getMaxPlayers() {
        return (int) options.get(OPTIONS.MAX_PLAYERS);
    }

    public String getMotd() {
        return String.valueOf(options.get(OPTIONS.MOTD));
    }

    void loadConf() {
        BungeeConfig conf = BungeeConfig.findById(1);
        Map<OPTIONS, Object> options2 = new HashMap<>();
        String dbConfig = conf.getPermissions();

        config = (Map) yaml.load(dbConfig);

        if (config == null) {
            config = new CaseInsensitiveMap();
        } else {
            config = new CaseInsensitiveMap(config);
        }
        config.put("timeout", 900000);
        config.put("uuid", "0-0-0-0");
        config.put("onlineMode", true);
        config.put("player_limit", -1);
        config.put("connection_throttle", -1);
        config.put("ip_forward", true);

        options2.put(OPTIONS.MAX_PLAYERS, conf.getMaxPlayers());
        options2.put(OPTIONS.MOTD, ChatColor.translateAlternateColorCodes('&', conf.getMotd()));
        BungeeInstance instance = BungeeInstance.findFirst("server_id = ?", BungeeGuardUtils.getServerID());

        options2.put(OPTIONS.BIND_ADDRESS, instance.getBindAddress());
        options = options2;

        List<BungeePremadeMessage> premadeMessages = BungeePremadeMessage.findAll();
        plugin.setPremadeMessages(premadeMessages);
        List<BungeeBlockedCommands> blockedCommands = BungeeBlockedCommands.findAll();
        plugin.setForbiddenCommands(blockedCommands);
        List<BungeeAnnouncements> announcements = BungeeAnnouncements.findAll();
        plugin.getAM().setAnnouncements(announcements);
        plugin.setBroadcastDelay(conf.getBroadcastDelay());
    }

    void loadServers() {
        List<BungeeServer> _s = BungeeServer.findAll();
        HashMap<String, ServerInfo> servers_new = new HashMap<>();
        for (BungeeServer serveur : _s) {
            String name = serveur.getName();
            String addr = serveur.getAddress();
            String prettyName = ChatColor.translateAlternateColorCodes('&', serveur.getPrettyName());
            String shortName = ChatColor.translateAlternateColorCodes('&', serveur.getShortName());
            String motd = "Serveur UHCGames"; // Should <not> be displayed.
            boolean restricted = false;
            plugin.addPrettyServerName(name, prettyName);
            plugin.addShortServerName(name, shortName);
            InetSocketAddress address = Util.getAddr(addr);
            ServerInfo info = ProxyServer.getInstance().constructServerInfo(name, address, motd, restricted);
            servers_new.put(name, info);
        }
        setServers(servers_new);
    }

    // Load listeners
    void loadListener() {
        if (listener != null)
            return;
        Map<OPTIONS, Object> options = getOptions();
        Map<String, String> forced = new HashMap<>();
        String motd = String.valueOf(options.get(OPTIONS.MOTD));

        int maxPlayers = (int) options.get(OPTIONS.MAX_PLAYERS);
        // Bungee n'apprécie pas trop qu'on change d'ip:port d'écoute quand il tourne, donc on attend reboot :)
        host = (host == null) ? (String) options.get(OPTIONS.BIND_ADDRESS) : host;

        String defaultServer = "hub";
        String fallbackServer = "limbo";
        boolean forceDefault = true;
        int tabListSize = 60;
        DefaultTabList value = DefaultTabList.SERVER;
        boolean setLocalAddress = false; // On laisse debian s'en occuper :}
        boolean pingPassthrough = false;
        boolean query = false;
        int queryPort = 25577;

        InetSocketAddress address = Util.getAddr(host);
        //noinspection ConstantConditions
        listener = new ListenerInfo(address, motd, maxPlayers, tabListSize, defaultServer, fallbackServer,
                forceDefault, forced, value.toString(), setLocalAddress, pingPassthrough, queryPort, query);

    }

    void loadForcedHosts() {
        Map<String, String> forced_hosts2 = new HashMap<>();
        List<BungeeForcedHost> bfh = BungeeForcedHost.findAll();
        for (BungeeForcedHost bf : bfh) {
            forced_hosts2.put(bf.getIp().toLowerCase(), bf.getServer());
        }
        forced_hosts = forced_hosts2;
    }

    /**
     * The default tab list options available for picking.
     */
    private enum DefaultTabList {

        GLOBAL(), GLOBAL_PING(), SERVER()
    }

    private enum OPTIONS {
        MAX_PLAYERS, MOTD, BIND_ADDRESS
    }
}