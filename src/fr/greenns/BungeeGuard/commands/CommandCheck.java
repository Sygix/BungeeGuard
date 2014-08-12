package fr.greenns.BungeeGuard.commands;

import java.util.UUID;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import fr.greenns.BungeeGuard.BungeeGuard;
import fr.greenns.BungeeGuard.BungeeGuardUtils;
import fr.greenns.BungeeGuard.utils.Ban;
import fr.greenns.BungeeGuard.utils.UUIDFetcher;

public class CommandCheck extends Command {

	public BungeeGuard plugin;

	public CommandCheck(BungeeGuard plugin) {
		super("check", "bungeeguard.check");
		this.plugin = plugin;
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		if(args.length != 1) {
			sender.sendMessage(new ComponentBuilder("Usage: /check <player>").color(ChatColor.RED).create());
		} else {
			ProxiedPlayer bannedPlayer = plugin.getProxy().getPlayer(args[0]);
			UUID bannedUUID;
			if(bannedPlayer == null) {
				try {
					bannedUUID = UUIDFetcher.getUUIDOf(args[0]);
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
				sender.sendMessage(new ComponentBuilder("Le joueur ").color(ChatColor.YELLOW).append(args[0]).color(ChatColor.AQUA).append(" n'est pas banni.").color(ChatColor.YELLOW).create());
			} else {
				sender.sendMessage(new ComponentBuilder("Le joueur ").color(ChatColor.YELLOW).append(args[0]).color(ChatColor.AQUA).append(" est banni.").color(ChatColor.YELLOW).create());
				if(Ban.getReason().equals(null)) sender.sendMessage(new ComponentBuilder("Raison:  ").color(ChatColor.YELLOW).append(Ban.getReason()).color(ChatColor.AQUA).create());
				if(Ban.getTime() != -1) sender.sendMessage(new ComponentBuilder("Pendant:  ").color(ChatColor.YELLOW).append(BungeeGuardUtils.getDuration(Ban.getTime())).color(ChatColor.AQUA).create());
				sender.sendMessage(new ComponentBuilder("Par:  ").color(ChatColor.YELLOW).append(Ban.getAdminName()).color(ChatColor.AQUA).create());
			}
		}
	}

}
