package fr.PunKeel.BungeeGuard;

import fr.PunKeel.BungeeGuard.Utils.DateUtil;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class BungeeGuardUtils {
    private static Main plugin;
    private static String server_id;

    public BungeeGuardUtils(Main plugin) {
        BungeeGuardUtils.plugin = plugin;
    }

    public static Long parseDuration(final String durationStr) {
        Long diff = DateUtil.parseDateDiff(durationStr, true);
        if (diff == null)
            return null;
        return diff - System.currentTimeMillis();
    }

    public static String getDuration(final long futureTimestamp) {
        if (futureTimestamp == -1)
            return "";
        if (futureTimestamp < 0)
            return "-" + getDuration(-futureTimestamp);
        return DateUtil.formatDateDiff(futureTimestamp, false);
    }

    public static String getServerID() {
        if (server_id != null)
            return server_id;
        Configuration config = null;
        try {
            config = ConfigurationProvider.getProvider(YamlConfiguration.class).load(new File(plugin.getDataFolder().getParent(), "RedisBungee/config.yml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (config != null) {
            server_id = String.valueOf(config.getString("server-id", "bungeeX"));
        }
        return server_id;
    }
}
