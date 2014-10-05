package net.uhcwork.BungeeGuard;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;
import net.uhcwork.BungeeGuard.Models.BungeeBan;
import net.uhcwork.BungeeGuard.Models.BungeeMute;
import net.uhcwork.BungeeGuard.utils.DateUtil;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class BungeeGuardUtils {
    public static Main plugin;
    private static String server_id;
    private static String staffBroadcastTag = ChatColor.GRAY + "" + ChatColor.BOLD + "[" + ChatColor.RED + "" + ChatColor.BOLD + "STAFF" + ChatColor.GRAY + "" + ChatColor.BOLD + "]" + ChatColor.GRAY;

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

    public static BungeeBan getBan(UUID u) {
        return plugin.getBM().findBan(u);
    }

    public static BungeeMute getMute(UUID u) {
        return plugin.getMM().findMute(u);
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

    public static String translateCodes(String message) {
        message = ChatColor.translateAlternateColorCodes('&', message).replaceAll("%n", "\n");
        // message = message.replaceAll("%timer", getDateFormat(plugin.time));
        return message;
    }

    public static String getStaffBroadcastTag() {
        return staffBroadcastTag;
    }

    @SuppressWarnings("deprecation")
    public static void msgPluginCommand(CommandSender sender) {
        sender.sendMessage(ChatColor.RED + "" + ChatColor.UNDERLINE
                + "                            " + ChatColor.RESET
                + "§6.: BungeeManager :." + ChatColor.RED + ""
                + ChatColor.UNDERLINE + "                           ");
        sender.sendMessage(ChatColor.RED + " ");
        sender.sendMessage(ChatColor.RED
                + " /ban <Player> [temps/0 pour définitif] [raison ...]");
        sender.sendMessage(ChatColor.RED + " /kick <Player> [raison ...]");
        sender.sendMessage(ChatColor.RED + " /unban <Player> [raison ...]");
        sender.sendMessage(ChatColor.RED + " /check <Player>");
        sender.sendMessage(ChatColor.RED + " /mute <Player>");
        sender.sendMessage(ChatColor.RED + " /unmute <Player>");
        sender.sendMessage(ChatColor.RED + " /silence (on/off)");
        sender.sendMessage(ChatColor.RED + " /say [message]");
        sender.sendMessage(ChatColor.RED + " /msg <Player> [message]");
        sender.sendMessage(ChatColor.RED + " /spychat (toggle)");
        sender.sendMessage(ChatColor.RED + " /motd (refresh)");
        sender.sendMessage(ChatColor.RED
                + ""
                + ChatColor.UNDERLINE
                + "                                                                               ");
    }

    public static String getCallingMethodInfo() {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        if (stackTrace != null && stackTrace.length >= 3) {
            StackTraceElement s = stackTrace[3];
            if (s != null) {
                return s.getClassName() + ".(" + s.getMethodName() + "):[" + s.getLineNumber() + "]";
            }
        }
        return null;
    }
}
