package fr.greenns.BungeeGuard.commands;

import java.util.ArrayList;
import java.util.List;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import fr.greenns.BungeeGuard.BungeeGuard;
import fr.greenns.BungeeGuard.Lobbies.Lobby;

public class CommandBCast extends Command {

	public BungeeGuard plugin;

	public CommandBCast(BungeeGuard plugin) {
		super("bcast", "bungeeguard.bcast");
		this.plugin = plugin;
	}

	@Override
	public void execute(CommandSender sender, String[] args) {

		if (args.length == 0) {
			plugin.utils.msgPluginCommand(sender);
			return;
		}

		if (args.length > 0) {
			String msg = "";
			for (int a = 0; a < args.length; a++) {
				if(a>0) msg += " ";
				msg += args[a];
			}

			msg = plugin.utils.translateCodes(msg);
			
			List<ServerInfo> serversList = new ArrayList<ServerInfo>();
			for(Lobby server : BungeeGuard.lobbys) {
				serversList.add(server.getServerInfo());
			}
			if(sender instanceof ProxiedPlayer) {
				ServerInfo currentServerInfo = ((ProxiedPlayer) sender).getServer().getInfo();
				if(!serversList.contains(currentServerInfo)) serversList.add(currentServerInfo);
			}

			for(ServerInfo server: serversList) {
				for(ProxiedPlayer p: server.getPlayers()) {
					p.sendMessage(new ComponentBuilder(" ").create());
					p.sendMessage(new ComponentBuilder(ChatColor.AQUA+"["+ChatColor.GOLD +"***"+ChatColor.AQUA+"]" + ChatColor.GRAY + " " + msg).create());
					p.sendMessage(new ComponentBuilder(" ").create());
				}
			}
		}
	}
}