package fr.greenns.BungeeGuard.commands;

import java.util.UUID;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import fr.greenns.BungeeGuard.BungeeGuard;
import fr.greenns.BungeeGuard.BungeeGuardUtils;
import fr.greenns.BungeeGuard.utils.Ban;
import fr.greenns.BungeeGuard.utils.BanType;
import fr.greenns.BungeeGuard.utils.UUIDFetcher;

public class CommandUnban extends Command {

	public BungeeGuard plugin;

	public CommandUnban(BungeeGuard plugin) {
		super("unban", "bungeeguard.unban");
		this.plugin = plugin;
	}

	@Override
	public void execute(CommandSender sender, String[] args) {

		if (args.length == 0) {
			sender.sendMessage(new ComponentBuilder("Usage: /unban <pseudo> [reason]").color(ChatColor.RED).create());
		}
		else if (args.length >= 1) {
			String unbanReason = "";
			String unbanName = (sender instanceof ProxiedPlayer) ? sender.getName() : "UHConsole";
			
			if (args.length > 1) {
				for (int i = 1; i < args.length; i++)
					unbanReason += " " + args[i];
			}
			String bannedName = args[0];
			
			ProxiedPlayer bannedPlayer = plugin.getProxy().getPlayer(bannedName);
			UUID bannedUUID;
			if(bannedPlayer == null) {
				try {
					bannedUUID = UUIDFetcher.getUUIDOf(bannedName);
					if(bannedUUID == null) {
						sender.sendMessage(new ComponentBuilder("Erreur: Ce joueur n'existe pas.").color(ChatColor.RED).create());
						return;
					}
				} catch (Exception e) {
					sender.sendMessage(new ComponentBuilder("Erreur lors de la récupération de l'UUID :").color(ChatColor.RED).append(e.getMessage()).color(ChatColor.GRAY).create());
					return;
				}
			} else {
				bannedUUID = bannedPlayer.getUniqueId();
			}
			
			Ban Ban = BungeeGuardUtils.getBan(bannedUUID);
			if(Ban == null) {
				sender.sendMessage(new ComponentBuilder("Erreur: Ce joueur n'est pas banni.").color(ChatColor.RED).create());
			} else {
				Ban.removeBanFromBDD(unbanReason, unbanName);
				
				BanType BanTypeVar = (unbanReason == "") ? BanType.UNBAN : BanType.UNBAN_W_REASON;
				String adminFormat = BanTypeVar.adminFormat("", unbanReason, unbanName, bannedName);
				BaseComponent[] message = new ComponentBuilder(adminFormat).create();
				for(ProxiedPlayer p: plugin.getProxy().getPlayers()) {
					if(p.hasPermission("bungeeguard.notify")) p.sendMessage(message);
				}
				System.out.print(adminFormat);
			}
		}
	}

}
