package fr.greenns.BungeeGuard.Kick;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import fr.greenns.BungeeGuard.BungeeGuard;
import fr.greenns.BungeeGuard.utils.ComponentManager;

public class CommandKick extends Command {

	public BungeeGuard plugin;

	public CommandKick(BungeeGuard plugin) {
		super("kick", "bungeeguard.kick");
		this.plugin = plugin;
	}
	
	@Override
	public void execute(CommandSender sender, String[] args) {
		String adminName = (sender instanceof ProxiedPlayer) ? sender.getName() : "UHConsole";
		
		if (args.length == 0) {
			sender.sendMessage(new ComponentBuilder("Usage: /kick <pseudo> [reason]").color(ChatColor.RED).create());
		} else {			
			String reason = "";
			if(args.length > 1) {
				for (int i = 1; i < args.length; i++){
					reason += " " + args[i];
				}
			}
			if(reason == "") reason = null;
			if(reason != null) reason = ChatColor.translateAlternateColorCodes('&', reason);
			
			KickType KickTypeVar = (reason != null) ? KickType.KICK_W_REASON : KickType.KICK;
			
			String bannedName = args[0];
			ProxiedPlayer bannedPlayer = plugin.getProxy().getPlayer(bannedName);
			if(bannedPlayer == null) {
				sender.sendMessage(new ComponentBuilder("Erreur: Ce joueur n'est pas en ligne.").color(ChatColor.RED).create());
				return;
			} else {
				bannedPlayer.disconnect(ComponentManager.generate(KickTypeVar.kickFormat(reason)));
			}
			
			String adminFormat = KickTypeVar.adminFormat(reason, adminName, bannedName);
			BaseComponent[] message = ComponentManager.generate(adminFormat);
			for(ProxiedPlayer p: plugin.getProxy().getPlayers()) {
				if(p.hasPermission("bungeeguard.notify")) {
					p.sendMessage(message);
				}
			}
			System.out.print(adminFormat);
		}
	}
}
