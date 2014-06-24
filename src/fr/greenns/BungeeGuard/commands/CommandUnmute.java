package fr.greenns.BungeeGuard.commands;

import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import fr.greenns.BungeeGuard.BungeeGuard;

public class CommandUnmute  extends Command {

	public BungeeGuard plugin;

	public CommandUnmute(BungeeGuard plugin)
	{
		super("unmute", "bungeeguard.mute");
		this.plugin = plugin;
	}

	public void execute(CommandSender sender, String[] args)
	{
		String name;
		if(sender instanceof ProxiedPlayer)
		{
			ProxiedPlayer p = (ProxiedPlayer)sender;

			if(!p.hasPermission("bungeeguard.mute"))	
			{
				return;
			}

			name = p.getName();
		}
		else
		{
			name = "*Console*";
		}

		if(args.length == 0)
		{
			return;
		}

		if(args.length > 0)
		{	
			if(plugin.mute.containsKey(args[0]))
			{
				plugin.mute.remove(args[0]);
				sender.sendMessage("§a"+args[0]+"§7 a été démuté !");
			}
			else
			{
				sender.sendMessage("§c"+args[0]+"§7 n'est pas mute !");
				return;
			}

			for (ProxiedPlayer playerdwa : BungeeCord.getInstance().getPlayers())
			{
				if(playerdwa.hasPermission("bungeeguard.notify"))
				{
					playerdwa.sendMessage(plugin.utils.staffBroadcast + "§a"+args[0]+" a été démuté par " + name);
				}
			}

			if(BungeeCord.getInstance().getPlayer(args[0]) != null)
			{
				ProxiedPlayer cel = BungeeCord.getInstance().getPlayer(args[0]);
				cel.sendMessage("§7Vous avez été §adémuté !");
			}
		}
	}
}
