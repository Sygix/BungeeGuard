package fr.greenns.BungeeGuard;

import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import fr.greenns.BungeeGuard.Models.BungeeBan;
import fr.greenns.BungeeGuard.Models.BungeeMute;
import fr.greenns.BungeeGuard.MultiBungee.MultiBungee;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BungeeGuardUtils {
    public static Main plugin;
    static Pattern timePattern = Pattern.compile("([0-9]+)([wdhms])");
    private static String server_id;
    private static String staffBroadcastTag = ChatColor.GRAY + "" + ChatColor.BOLD + "[" + ChatColor.RED + "" + ChatColor.BOLD + "STAFF" + ChatColor.GRAY + "" + ChatColor.BOLD + "]" + ChatColor.GRAY;

    public BungeeGuardUtils(Main plugin) {
        BungeeGuardUtils.plugin = plugin;
    }

    public static long parseDuration(final String durationStr) {
        Matcher m = timePattern.matcher(durationStr);
        int time = -1;
        while (m.find()) {
            if (m.group() == null || m.group().isEmpty()) {
                continue;
            }
            if (m.group(1) != null && !m.group(1).isEmpty() && m.group(2) != null && !m.group(2).isEmpty()) {
                int number = Integer.parseInt(m.group(1));
                String type = m.group(2);
                switch (type) {
                    case "w":
                        time += number * 604800000L;
                        break;
                    case "d":
                        time += number * 86400000L;
                        break;
                    case "h":
                        time += number * 3600000L;
                        break;
                    case "m":
                        time += number * 60000L;
                        break;
                    case "s":
                        time += number * 1000L;
                        break;
                }
            }
        }
        return time;
    }

    public static String getDuration(final long futureTimestamp) {
        if (futureTimestamp == -1)
            return "";
        if (futureTimestamp < 0)
            return "-" + getDuration(futureTimestamp);
        int seconds = (int) ((futureTimestamp - System.currentTimeMillis()) / 1000);
        Preconditions.checkArgument(seconds > 0, "Le timestamp doit etre supérieur au current timestamp !");

        final List<String> item = new ArrayList<String>();

        int years = 0;
        while (seconds >= 31536000) {
            years++;
            seconds -= 31536000;
        }
        if (years > 0) {
            item.add(years + " an" + ((years != 1) ? "s" : ""));
        }

        int months = 0;
        while (seconds >= 2592000) {
            months++;
            seconds -= 2592000;
        }
        if (months > 0) {
            item.add(months + " mois");
        }

        int weeks = 0;
        while (seconds >= 604800) {
            weeks++;
            seconds -= 604800;
        }
        if (weeks > 0) {
            item.add(weeks + " semaine" + ((weeks != 1) ? "s" : ""));
        }

        int days = 0;
        while (seconds >= 86400) {
            days++;
            seconds -= 86400;
        }
        if (days > 0) {
            item.add(days + " jour" + ((days != 1) ? "s" : ""));
        }

        int hours = 0;
        while (seconds >= 3600) {
            hours++;
            seconds -= 3600;
        }
        if (hours > 0) {
            item.add(hours + " heure" + ((hours != 1) ? "s" : ""));
        }

        int mins = 0;
        while (seconds >= 60) {
            mins++;
            seconds -= 60;
        }
        if (mins > 0) {
            item.add(mins + " minute" + ((mins != 1) ? "s" : ""));
        }

        if (seconds > 0) {
            item.add(seconds + " seconde" + ((seconds != 1) ? "s" : ""));
        }

        return Joiner.on(", ").join(item);
    }

    public static String getFinalArg(String[] args, int start) {
        StringBuilder sb = new StringBuilder();
        for (int i = start; i < args.length; i++) {
            if (i != start) {
                sb.append(" ");
            }
            sb.append(args[i]);
        }
        String msg = sb.toString();
        sb.setLength(0);
        return msg;
    }

    public static BungeeBan getBan(UUID u) {
        return plugin.getBM().findBan(u);
    }

    public static BungeeMute getMute(UUID u) {
        return plugin.getMM().findMute(u);
    }

    public static MultiBungee getMB() {
        if (plugin.getMB() == null) {
            return null;
        }
        return plugin.getMB();
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

    public boolean isParsableToInt(String i) {
        try {
            Integer.parseInt(i);
            return true;
        } catch (NumberFormatException nfe) {
            return false;
        }
    }

    public String getDateFormat(long time) {
        Date date = new Date(time);
        String dateString = new SimpleDateFormat("HH:mm:ss").format(date);

        return dateString;
    }

    @SuppressWarnings("deprecation")
    public static void msgPluginCommand(CommandSender sender) {
        sender.sendMessage(ChatColor.RED + "" + ChatColor.UNDERLINE
                + "                            " + ChatColor.RESET
                + "§6.: BungeeManager :." + ChatColor.RED + ""
                + ChatColor.UNDERLINE + "                           ");
        sender.sendMessage(ChatColor.RED + " ");
        sender.sendMessage(ChatColor.RED
                + " /ban <Player> [temps en minutes/0 pour définitif] [raison ...]");
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
}
