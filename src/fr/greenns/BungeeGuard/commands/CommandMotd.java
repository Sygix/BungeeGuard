package fr.greenns.BungeeGuard.commands;

import fr.greenns.BungeeGuard.BungeeGuard;
import fr.greenns.BungeeGuard.BungeeGuardUtils;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.plugin.Command;

/**
 * Created by Greenns on 11/07/14.
 */
public class CommandMotd extends Command {

	public BungeeGuard plugin;

	public CommandMotd(BungeeGuard plugin) {
		super("motd", "bungeeguard.motd");
		this.plugin = plugin;
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		if (args.length == 0) {
            BungeeGuardUtils.getMB().sendChannelMessage("refreshMOTD", "");
			plugin.utils.refreshMotd();
			sender.sendMessage(new ComponentBuilder("Motd updated !").color(ChatColor.GREEN).create());
        }
	}
}