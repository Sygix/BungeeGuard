package fr.greenns.BungeeGuard.commands;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import fr.greenns.BungeeGuard.BungeeGuard;

public class CommandHelp extends Command {

	public BungeeGuard plugin;

	public CommandHelp(BungeeGuard plugin)
	{
		super("help", "bungeeguard.help");
		this.plugin = plugin;
	}

	public void execute(CommandSender sender, String[] args)
	{
		if(sender instanceof ProxiedPlayer)
		{
			ProxiedPlayer p = (ProxiedPlayer)sender;

			if(!p.hasPermission("bungeeguard.help"))	
			{
				return;
			}
		}

		if(args.length == 0)
		{
			sender.sendMessage("");
			return;
		}

		else
		{
			
		}
	}

}
