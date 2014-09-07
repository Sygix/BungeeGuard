package fr.greenns.BungeeGuard.Config;

import fr.greenns.BungeeGuard.BungeeGuard;
import fr.greenns.BungeeGuard.BungeeGuardUtils;
import fr.greenns.BungeeGuard.utils.MultiBungee;
import net.md_5.bungee.Util;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ConfigurationAdapter;
import net.md_5.bungee.api.config.ListenerInfo;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.util.CaseInsensitiveMap;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class MysqlConfigAdapter implements ConfigurationAdapter {
    private final Yaml yaml;
    private Map config;
    private BungeeGuard plugin;

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

            PreparedStatement query = plugin.sql.prepare("SELECT bungeeConf FROM BungeeGuard_Config " +
                    "WHERE server_id IN ('', ?) " +
                    "ORDER BY LENGTH(server_id) DESC " +
                    "LIMIT 0,1");
            try {
                String server_id = "";
                MultiBungee MB = BungeeGuardUtils.getMB();
                if (MB != null)
                    server_id = MB.getServerId();
                query.setString(1, server_id);
                ResultSet result = query.executeQuery();
                result.next();
                dbConfig = result.getString("bungeeConf");
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
        System.out.println("[MysqlConfig] Method unsupported: save");
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
        Map<String, Map<String, Object>> base = get("servers", (Map) Collections.singletonMap("lobby", new HashMap<>()));
        Map<String, ServerInfo> ret = new HashMap<>();

        for (Map.Entry<String, Map<String, Object>> entry : base.entrySet()) {
            Map<String, Object> val = entry.getValue();
            String name = entry.getKey();
            String addr = get("address", "localhost:25565", val);
            String motd = ChatColor.translateAlternateColorCodes('&', get("motd", "&1Just another BungeeCord - Forced Host", val));
            boolean restricted = get("restricted", false, val);
            InetSocketAddress address = Util.getAddr(addr);
            ServerInfo info = ProxyServer.getInstance().constructServerInfo(name, address, motd, restricted);
            ret.put(name, info);
        }

        return ret;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Collection<ListenerInfo> getListeners() {
        Collection<Map<String, Object>> base = get("listeners", (Collection) Arrays.asList(new HashMap()));
        Map<String, String> forcedDef = new HashMap<>();
        forcedDef.put("pvp.md-5.net", "pvp");

        Collection<ListenerInfo> ret = new HashSet<>();

        for (Map<String, Object> val : base) {
            String motd = get("motd", "&1Another Bungee server", val);
            motd = ChatColor.translateAlternateColorCodes('&', motd);

            int maxPlayers = get("max_players", 1, val);
            String defaultServer = get("default_server", "lobby", val);
            String fallbackServer = get("fallback_server", defaultServer, val);
            boolean forceDefault = get("force_default_server", false, val);
            String host = get("host", getHost(), val);
            int tabListSize = get("tab_size", 60, val);
            InetSocketAddress address = Util.getAddr(host);
            Map<String, String> forced = new CaseInsensitiveMap<>(get("forced_hosts", forcedDef, val));
            String tabListName = get("tab_list", "SERVER", val);
            DefaultTabList value = DefaultTabList.valueOf(tabListName.toUpperCase());
            if (value == null) {
                value = DefaultTabList.SERVER;
            }
            boolean setLocalAddress = get("bind_local_address", true, val);
            boolean pingPassthrough = get("ping_passthrough", false, val);

            boolean query = get("query_enabled", false, val);
            int queryPort = get("query_port", 25577, val);

            ListenerInfo info = new ListenerInfo(address, motd, maxPlayers, tabListSize, defaultServer, fallbackServer, forceDefault, forced, value.toString(), setLocalAddress, pingPassthrough, queryPort, query);
            ret.add(info);
        }

        return ret;
    }

    private String getHost() {
        try {
            return Files.readAllLines(Paths.get("HOST"), StandardCharsets.UTF_8).get(0).trim();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "0.0.0.0:25565";
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

    /**
     * The default tab list options available for picking.
     */
    private enum DefaultTabList {

        GLOBAL(), GLOBAL_PING(), SERVER();
    }
}