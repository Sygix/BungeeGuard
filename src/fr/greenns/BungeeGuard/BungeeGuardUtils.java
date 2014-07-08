package fr.greenns.BungeeGuard;

import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class BungeeGuardUtils {

	public BungeeGuard plugin;
	public String staffBroadcast = "§7§l[§cSTAFF§7]§r ";
    private static final Pattern timePattern = Pattern.compile("(?:([0-9]+)\\s*y[a-z]*[,\\s]*)?(?:([0-9]+)\\s*mo[a-z]*[,\\s]*)?(?:([0-9]+)\\s*w[a-z]*[,\\s]*)?(?:([0-9]+)\\s*d[a-z]*[,\\s]*)?(?:([0-9]+)\\s*h[a-z]*[,\\s]*)?(?:([0-9]+)\\s*m[a-z]*[,\\s]*)?(?:([0-9]+)\\s*(?:s[a-z]*)?)?", 2);

	public BungeeGuardUtils(BungeeGuard plugin)
	{
		this.plugin = plugin;
	}
	
	public boolean isParsableToInt(String i) {
		try {
			Integer.parseInt(i);
			return true;
		} catch (NumberFormatException nfe) {
			return false;
		}
	}
	
	public void msgPluginCommand(CommandSender sender)
	{
		sender.sendMessage(ChatColor.RED + "" + ChatColor.UNDERLINE + "                            "+ChatColor.RESET+"§6.: BungeeManager :."+ ChatColor.RED + "" + ChatColor.UNDERLINE +"                           ");
		sender.sendMessage(ChatColor.RED + " ");
		sender.sendMessage(ChatColor.RED + " /ban <Player> [temps en minutes/0 pour définitif] [raison ...]");
		sender.sendMessage(ChatColor.RED + " /kick <Player> [raison ...]");
		sender.sendMessage(ChatColor.RED + " /unban <Player> [raison ...]");
		sender.sendMessage(ChatColor.RED + " /check <Player>");
		sender.sendMessage(ChatColor.RED + " /mute <Player>");
		sender.sendMessage(ChatColor.RED + " /unmute <Player>");
		sender.sendMessage(ChatColor.RED + " /silence (on/off)");
		sender.sendMessage(ChatColor.RED + " /say [message]");
		sender.sendMessage(ChatColor.RED + " /msg <Player> [message]");
		sender.sendMessage(ChatColor.RED + " /spychat (toggle)");
		sender.sendMessage(ChatColor.RED + "" + ChatColor.UNDERLINE + "                                                                               ");
	}

    public static long parseDuration(final String durationStr) throws IllegalArgumentException {
        final Matcher m = timePattern.matcher(durationStr);
        int years = 0;
        int months = 0;
        int weeks = 0;
        int days = 0;
        int hours = 0;
        int minutes = 0;
        int seconds = 0;
        boolean found = false;
        while (m.find()) {
            if (m.group() == null || m.group().isEmpty()) {
                continue;
            }
            for (int i = 0; i < m.groupCount(); i++) {
                if (m.group(i) != null && !m.group(i).isEmpty()) {
                    found = true;
                    break;
                }
            }
            if (found) {
                if (m.group(1) != null && !m.group(1).isEmpty()) {
                    years = Integer.parseInt(m.group(1));
                }
                if (m.group(2) != null && !m.group(2).isEmpty()) {
                    months = Integer.parseInt(m.group(2));
                }
                if (m.group(3) != null && !m.group(3).isEmpty()) {
                    weeks = Integer.parseInt(m.group(3));
                }
                if (m.group(4) != null && !m.group(4).isEmpty()) {
                    days = Integer.parseInt(m.group(4));
                }
                if (m.group(5) != null && !m.group(5).isEmpty()) {
                    hours = Integer.parseInt(m.group(5));
                }
                if (m.group(6) != null && !m.group(6).isEmpty()) {
                    minutes = Integer.parseInt(m.group(6));
                }
                if (m.group(7) != null && !m.group(7).isEmpty()) {
                    seconds = Integer.parseInt(m.group(7));
                }
                break;
            }
        }
        if (!found) {
            throw new IllegalArgumentException(ChatColor.RED + "Invalid duration !");
        }
        final Calendar c = new GregorianCalendar();
        if (years > 0) {
            c.add(Calendar.YEAR, years);
        }
        if (months > 0) {
            c.add(Calendar.MONTH, months);
        }
        if (weeks > 0) {
            c.add(Calendar.WEEK_OF_YEAR, weeks);
        }
        if (days > 0) {
            c.add(Calendar.DAY_OF_MONTH, days);
        }
        if (hours > 0) {
            c.add(Calendar.HOUR_OF_DAY, hours);
        }
        if (minutes > 0) {
            c.add(Calendar.MINUTE, minutes);
        }
        if (seconds > 0) {
            c.add(Calendar.SECOND, seconds);
        }
        return c.getTimeInMillis();
    }


    public static String getDuration(final long futureTimestamp) {
        int seconds = (int) ((futureTimestamp - System.currentTimeMillis()) / 1000) + 1;
        Preconditions.checkArgument(seconds > 0,
                "The timestamp passed in parameter must be superior to the current timestamp !");

        final List<String> item = new ArrayList<String>();

        int months = 0;
        while (seconds >= 2678400) {
            months++;
            seconds -= 2678400;
        }
        if (months > 0) {
            item.add(months + " months");
        }

        int days = 0;
        while (seconds >= 86400) {
            days++;
            seconds -= 86400;
        }
        if (days > 0) {
            item.add(days + " days");
        }

        int hours = 0;
        while (seconds >= 3600) {
            hours++;
            seconds -= 3600;
        }
        if (hours > 0) {
            item.add(hours + " hours");
        }

        int mins = 0;
        while (seconds >= 60) {
            mins++;
            seconds -= 60;
        }
        if (mins > 0) {
            item.add(mins + " mins");
        }

        if (seconds > 0) {
            item.add(seconds + " secs");
        }

        return Joiner.on(", ").join(item);
    }

}
