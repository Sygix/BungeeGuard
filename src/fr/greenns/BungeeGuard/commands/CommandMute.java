package fr.greenns.BungeeGuard.commands;

import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import fr.greenns.BungeeGuard.BungeeGuard;

public class CommandMute extends Command {

	public BungeeGuard plugin;
	public int czas;

	public CommandMute(BungeeGuard plugin)
	{
		super("mute", "bungeeguard.mute");
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
			name = p.getDisplayName();
		}
		else
		{
			name = "*Console*";
		}

		if(args.length == 0)
		{
			plugin.utils.msgPluginCommand(sender);
			return;
		}

		String msg = "";
		String targetmsg = "";
		String powodd = "";
		String time = "0"; // normal non unix time
		String days ="";
		String hours ="";
		String minutes ="";
		String nick = "";
		int minuteIncrease = 0;
		int hourIncrease = 0;
		int dateIncrease = 0;
		czas = 0; // ban time in in unix settings
		int initialMinute;
		boolean onlinePlayer = false;
		boolean noTime = false;

		if(args.length > 0)
		{
			if(BungeeCord.getInstance().getPlayer(args[0]) != null)
			{
				ProxiedPlayer cel = BungeeCord.getInstance().getPlayer(args[0]);
				nick = cel.getName();
				onlinePlayer = true;
			}
			else
			{
				nick = args[0];
				onlinePlayer = false;
				if(nick.isEmpty())
				{
					sender.sendMessage("§cNom du joueur incorrecte ...");
					return;
				}
			}

			if(args.length == 1)
			{
				targetmsg = "§cVous etes muté 24 heures par " + name +" !";
				msg = "§c" + name + " a muté 24 heures " + nick + " !";

				long unixTime = System.currentTimeMillis() / 1000L;
				czas = (int)(unixTime+(1440*60));
			}
		}
		if(args.length > 1)
		{
			initialMinute = Integer.valueOf(args[1]);

			if(args[0] != null)
			{
				if(plugin.utils.isParsableToInt(args[1]))
				{
					if(Integer.valueOf(args[1])<=0)
					{
						time = args[1] = "1440";
					}
					if(Integer.valueOf(args[1])>1440)
					{
						sender.sendMessage("§cLe temps de mute maximum est de 24 heures !");
						return;
					}
					else
					{
						long unixTime = System.currentTimeMillis() / 1000L;
						czas = (int)(unixTime+(Integer.valueOf(args[1])*60));
						time = args[1]=args[1];
					}
				}
				else
				{
					time = args[1] = "1440";
				}
			}
			else
			{
				time = args[1] = "1440";
			}

			dateIncrease = initialMinute / 1440;
			initialMinute = initialMinute - (1440*dateIncrease);
			hourIncrease = initialMinute / 60;
			initialMinute = initialMinute - (60*hourIncrease);
			minuteIncrease = initialMinute;

			if(dateIncrease == 0)
			{
				days ="";
			}
			if(hourIncrease == 0)
			{
				hours ="";
			}
			if(minuteIncrease == 0)
			{
				minutes ="";
			}

			if(dateIncrease != 0)
			{
				if(dateIncrease == 1)
				{
					days=dateIncrease + " jour ";
				}
				else
				{
					days=dateIncrease + " jours ";
				}
			}
			if(hourIncrease != 0)
			{
				if(hourIncrease == 1)
				{
					hours=hourIncrease + " heure ";
				}
				else
				{
					hours=hourIncrease + " heures ";
				}
			}
			if(minuteIncrease != 0)
			{
				if(minuteIncrease == 1)
				{
					minutes=minuteIncrease + " minute ";
				}
				else
				{
					minutes=minuteIncrease + " minutes ";
				}
			}

			if(minuteIncrease == 0 && hourIncrease == 0 && dateIncrease == 0)
			{
				targetmsg = "§cVous etes muté 24 heures par " + name +" !";
				msg = "§c" + name + " a muté 24 heures " + nick + " !";
			}
			else
			{
				targetmsg = "§cVous etes muté pour " + days  + hours  + minutes + "par " + name +" !";
				msg = "§c" + name + " a muté " + nick + " " + days  + hours  + minutes + "!";
			}
		}
		if(args.length > 2)
		{
			powodd = "";
			if ( noTime )
				for( int a=1; a<args.length;a++)powodd += " "+args[a];
			else
				for( int a=2; a<args.length;a++)powodd += " "+args[a];

			if(minuteIncrease == 0 && hourIncrease == 0 && dateIncrease == 0)
			{
				targetmsg = "§cVous etes muté 24 heures par " + name +" pour: " + powodd + " !";
				msg = "§c" + name + " a muté 24 heures " + nick + " pour:" + powodd + " !";
			}
			else
			{
				targetmsg = "§cVous etes bannis pour " + days  + hours  + minutes + "par " + name+" pour: " + powodd + " !";
				msg = "§c" + name + " a bannis " + nick + " " + days  + hours  + minutes + "pour:" + powodd + " !";
			}
		}


		if(onlinePlayer)
		{
			ProxiedPlayer cel = BungeeCord.getInstance().getPlayer(args[0]);

			if(plugin.mute.containsKey(cel.getName()))
			{
				sender.sendMessage("§c"+cel.getName()+" est deja mute !");
				return;
			}
			else
			{
				plugin.mute.put(cel.getName(),czas);
			}
			cel.sendMessage(targetmsg);
		}
		else
		{
			if(plugin.mute.containsKey(args[0]))
			{
				sender.sendMessage("§c"+args[0]+" est deja mute !");
				return;
			}
			else
			{
				plugin.mute.put(args[0],czas);
			}
		}


		System.out.println(msg);

		for (ProxiedPlayer playerdwa : BungeeCord.getInstance().getPlayers())
		{
			if(playerdwa.hasPermission("bungeeguard.notify"))
			{
				playerdwa.sendMessage(plugin.utils.staffBroadcast + msg);
			}
		}
	}
}
