package fr.greenns.BungeeGuard;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;


public class BungeeGuardUtils {

	public BungeeGuard plugin;
	public String staffBroadcast = "§7§l[§cSTAFF§7]§r ";

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
		sender.sendMessage(ChatColor.RED + "" + ChatColor.UNDERLINE + "                                                                               ");
	}

}
