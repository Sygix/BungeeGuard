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
    Collection<ListenerInfo> listeners = new HashSet<>();
    private Map<String, Object> config;
    private Main plugin;
    private String host;
    private BungeeConfig conf;
    private HashMap<String, ServerInfo> servers = new HashMap<>();


    public MysqlConfigAdapter(Main plugin) {
        this.plugin = plugin;
        DumperOptions options = new DumperOptions();
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        yaml = new Yaml(options);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void load() {
        final MysqlConfigAdapter _self = this;
        Future<Void> x = plugin.executePersistenceRunnable(new VoidRunner() {
            @Override
            protected void run() {
                conf = BungeeConfig.findById(1);
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


                Map<String, Object> permissions = get("permissions", new HashMap<String, Object>());
                if (permissions.isEmpty()) {
                    permissions.put("default", Arrays.asList("bungeecord.command.server", "bungeecord.command.list"));
                    permissions.put("admin", Arrays.asList("bungeecord.command.alert", "bungeecord.command.end", "bungeecord.command.ip", "bungeecord.command.reload"));
                }

                Map<String, Object> groups = get("groups", new HashMap<String, Object>());
                if (groups.isEmpty()) {
                    groups.put("md_5", Collections.singletonList("admin"));
                }

                List<BungeePremadeMessage> premadeMessages = BungeePremadeMessage.findAll();
                plugin.setPremadeMessages(premadeMessages);
                List<BungeeBlockedCommands> blockedCommands = BungeeBlockedCommands.findAll();
                plugin.setForbiddenCommands(blockedCommands);
                List<BungeeAnnouncements> announcements = BungeeAnnouncements.findAll();
                plugin.getAM().setAnnouncements(announcements);
                plugin.setBroadcastDelay(conf.getBroadcastDelay());

                // Load servers
                plugin.resetPrettyServerNames();
                plugin.resetShortServerNames();
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
                _self.setServers(servers_new);

                // Load listeners

                Map<OPTIONS, Object> options = getOptions();
                Map<String, String> forced = getForcedHosts();
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

                ListenerInfo info;

                InetSocketAddress address = Util.getAddr(host);
                //noinspection ConstantConditions
                info = new ListenerInfo(address, motd, maxPlayers, tabListSize, defaultServer, fallbackServer, forceDefault, forced, value.toString(), setLocalAddress, pingPassthrough, queryPort, query);
                listeners.add(info);
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
        return listeners;
    }

    private Map<OPTIONS, Object> getOptions() {
        Map<OPTIONS, Object> options = new HashMap<>();
        conf = BungeeConfig.findById(1);
        options.put(OPTIONS.MAX_PLAYERS, conf.getMaxPlayers());
        options.put(OPTIONS.MOTD, ChatColor.translateAlternateColorCodes('&', conf.getMotd()));
        Main.setMotd(String.valueOf(options.get(OPTIONS.MOTD)));
        BungeeInstance instance = BungeeInstance.findFirst("server_id = ?", BungeeGuardUtils.getServerID());

        options.put(OPTIONS.BIND_ADDRESS, instance.getBindAddress());
        return options;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Collection<String> getGroups(String player) {
        Collection<String> groups = get("groups." + player, null);
        Collection<String> ret = (groups == null) ? new HashSet<String>() : new HashSet<>(groups);
        ret.add("default");
        return ret;
    }

    @Override
    public Collection<?> getList(String path, Collection<?> def) {
        return get(path, def);
    }

    @Override
    public Collection<String> getPermissions(String group) {
        return get("permissions." + group, Collections.<String>emptySet());
    }

    public Map<String, String> getForcedHosts() {
        Map<String, String> forced_hosts = new HashMap<>();
        List<BungeeForcedHost> bfh = BungeeForcedHost.findAll();
        for (BungeeForcedHost bf : bfh) {
            forced_hosts.put(bf.getIp().toLowerCase(), bf.getServer());
        }
        return forced_hosts;
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