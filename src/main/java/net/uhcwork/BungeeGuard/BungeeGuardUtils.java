package net.uhcwork.BungeeGuard;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;
import net.uhcwork.BungeeGuard.Utils.DateUtil;

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
        return DateUtil.formatDateDiff(futureTimestamp);
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

    @SuppressWarnings("deprecation")
    public static void msgPluginCommand(CommandSender sender) {
        sender.sendMessage(ChatColor.RED + ".: BungeeGuard :.");
        sender.sendMessage(ChatColor.GRAY + "------------");
        sender.sendMessage(ChatColor.RED + " /ban <player> [temps/0 pour définitif] [raison ...]");
        sender.sendMessage(ChatColor.RED + " /unban <Player> [raison ...]");
        sender.sendMessage(ChatColor.RED + " /kick <Player> [raison ...]");
        sender.sendMessage(ChatColor.RED + " /mute <Player>");
        sender.sendMessage(ChatColor.RED + " /unmute <Player>");
        sender.sendMessage(ChatColor.RED + " /check <Player>");
        sender.sendMessage(ChatColor.RED + " /spychat (toggle)");
        sender.sendMessage(ChatColor.RED + " /silence (on/off)");
        sender.sendMessage(ChatColor.RED + " /say [message]");
        sender.sendMessage(ChatColor.RED + " /msg <Player> [message]");
        sender.sendMessage(ChatColor.RED + " /b:rl (refresh)");
        sender.sendMessage(ChatColor.RED + " /b:load (refresh)");
        sender.sendMessage(ChatColor.RED + " /gtp <pseudo>");
        sender.sendMessage(ChatColor.RED + " /find <pseudo>");
        sender.sendMessage(ChatColor.RED + " /ip <pseudo>");
        sender.sendMessage(ChatColor.RED + " /wallet");
        sender.sendMessage(ChatColor.RED + " /user");
        sender.sendMessage(ChatColor.RED + " /groups");
        sender.sendMessage(ChatColor.RED + " /staff");
        sender.sendMessage(ChatColor.RED + " /send");
        sender.sendMessage(ChatColor.RED + " /server");
        sender.sendMessage(ChatColor.RED + " /party");
        sender.sendMessage(ChatColor.GRAY + "------------");
    }
}
