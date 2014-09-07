package fr.greenns.BungeeGuard.Config;

import fr.greenns.BungeeGuard.BungeeGuard;
import fr.greenns.BungeeGuard.BungeeGuardUtils;
import net.md_5.bungee.Util;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ConfigurationAdapter;
import net.md_5.bungee.api.config.ListenerInfo;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.util.CaseInsensitiveMap;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.net.InetSocketAddress;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class MysqlConfigAdapter implements ConfigurationAdapter {
    private final Yaml yaml;
    private Map config;
    private BungeeGuard plugin;
    private String localBinding;

    public MysqlConfigAdapter(BungeeGuard plugin) {
        this.plugin = plugin;
        DumperOptions options = new DumperOptions();
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        yaml = new Yaml(options);
    }

    @Override
    public void load() {
        String dbConfig = "";

        plugin.sql.open();

        if (plugin.sql.getConnection() == null) {
            System.out.println("[b:rl] Erreur, checkConnection");
        } else {
            ResultSet res = plugin.sql.query("SELECT permissions FROM bungee_config LIMIT 0,1");
            try {
                res.next();
                dbConfig = res.getString("permissions");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

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
        if (plugin.sql.getConnection() == null) {
            plugin.sql.open();
        }

        ResultSet res = plugin.sql.query("SELECT name, address FROM bungee_servers;");
        Map<String, ServerInfo> ret = new HashMap<>();
        try {
            while (res.next()) {
                String name = res.getString("name");
                String addr = res.getString("address");
                String motd = "Serveur UHCGames"; // Should <not> be displayed.
                boolean restricted = false;
                InetSocketAddress address = Util.getAddr(addr);
                ServerInfo info = ProxyServer.getInstance().constructServerInfo(name, address, motd, restricted);
                ret.put(name, info);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return ret;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Collection<ListenerInfo> getListeners() {
        Map<OPTIONS, Object> options = new HashMap<>();
        Map<String, String> forced = new HashMap<>();

        try {
            options = getOptions();
            forced = getForcedHosts();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        String motd = ChatColor.translateAlternateColorCodes('&', String.valueOf(options.get(OPTIONS.MOTD)));

        int maxPlayers = (int) options.get(OPTIONS.MAX_PLAYERS);
        String host = (String) options.get(OPTIONS.BIND_ADDRESS);
        InetSocketAddress address = Util.getAddr(host);

        String defaultServer = "hub";
        String fallbackServer = "limbo";
        boolean forceDefault = true;
        int tabListSize = 60;
        DefaultTabList value = DefaultTabList.SERVER;
        boolean setLocalAddress = true;
        boolean pingPassthrough = false;
        boolean query = false;
        int queryPort = 25577;

        ListenerInfo info = new ListenerInfo(address, motd, maxPlayers, tabListSize, defaultServer, fallbackServer, forceDefault, forced, value.toString(), setLocalAddress, pingPassthrough, queryPort, query);


        Collection<ListenerInfo> ret = new HashSet<>();
        ret.add(info);
        if (localBinding != null && !localBinding.isEmpty()) {
            info = new ListenerInfo(Util.getAddr(localBinding), motd, maxPlayers, tabListSize, defaultServer, fallbackServer, forceDefault, forced, value.toString(), setLocalAddress, pingPassthrough, queryPort, query);
            ret.add(info);
        }
        return ret;
    }

    private Map<OPTIONS, Object> getOptions() throws SQLException {
        if (plugin.sql.getConnection() == null) {
            plugin.sql.open();
        }
        Map<OPTIONS, Object> options = new HashMap<>();
        ResultSet res = plugin.sql.query("SELECT motd, max_players FROM bungee_config LIMIT 0,1;");
        res.next();

        options.put(OPTIONS.MAX_PLAYERS, res.getInt("max_players"));
        options.put(OPTIONS.MOTD, ChatColor.translateAlternateColorCodes('&', res.getString("motd")));
        plugin.setMotd(String.valueOf(options.get(OPTIONS.MOTD)));

        PreparedStatement q = plugin.sql.prepare("SELECT bind_address, localBinding FROM bungee_instances WHERE server_id = ? LIMIT 0,1;");
        String server_id = BungeeGuardUtils.getServerID();
        q.setString(1, server_id);
        res = q.executeQuery();
        res.next();

        options.put(OPTIONS.BIND_ADDRESS, res.getString("bind_address"));
        localBinding = res.getString("localBinding");

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
    @SuppressWarnings("unchecked")
    public Collection<String> getPermissions(String group) {
        return get("permissions." + group, Collections.EMPTY_LIST);
    }

    public Map<String, String> getForcedHosts() throws SQLException {

        if (plugin.sql.getConnection() == null) {
            plugin.sql.open();
        }
        Map<String, String> forced_hosts = new HashMap<>();
        ResultSet res = plugin.sql.query("SELECT ip, to_server FROM bungee_forced_host");
        while (res.next()) {
            forced_hosts.put(res.getString("ip"), res.getString("to_server"));
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