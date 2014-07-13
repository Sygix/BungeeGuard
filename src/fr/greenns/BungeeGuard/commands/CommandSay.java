package fr.greenns.BungeeGuard.commands;

import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import fr.greenns.BungeeGuard.BungeeGuard;

public class CommandSay extends Command {

	public BungeeGuard plugin;

	public CommandSay(BungeeGuard plugin)
	{
		super("say", "bungeeguard.say");
		this.plugin = plugin;
	}

	public void execute(CommandSender sender, String[] args)
	{
		if(sender instanceof ProxiedPlayer)
		{
			ProxiedPlayer p = (ProxiedPlayer)sender;

			if(!p.hasPermission("bungeeguard.say"))	
			{
				return;
			}
		}

		if(args.length == 0)
		{
			plugin.utils.msgPluginCommand(sender);
			return;
		}

		if(args.length > 0)
		{
			String msg = "";
			for( int a=0; a<args.length;a++)msg += " "+args[a];
			
			msg = plugin.utils.translateCodes(msg);
			
			for (ProxiedPlayer p : BungeeCord.getInstance().getPlayers())
			{
                p.sendMessage(" ");
                p.sendMessage("§b[§a***§b]"+ ChatColor.RESET +"§7"+msg);
                p.sendMessage(" ");
			}
		}
	}
}