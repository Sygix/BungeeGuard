package fr.greenns.BungeeGuard.Mute;

import java.util.UUID;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import fr.greenns.BungeeGuard.BungeeGuard;
import fr.greenns.BungeeGuard.BungeeGuardUtils;
import fr.greenns.BungeeGuard.utils.ComponentManager;
import fr.greenns.BungeeGuard.utils.UUIDFetcher;

public class CommandUnmute extends Command {

	public BungeeGuard plugin;

	public CommandUnmute(BungeeGuard plugin) {
		super("unmute", "bungeeguard.mute");
		this.plugin = plugin;
	}

	@Override
	public void execute(CommandSender sender, String[] args) {

		if (args.length == 0) {
			sender.sendMessage(new ComponentBuilder("Usage: /unmute <pseudo> [reason]").color(ChatColor.RED).create());
		}
		else if (args.length >= 1) {
			String unmuteReason = "";
			String unmuteName = (sender instanceof ProxiedPlayer) ? sender.getName() : "UHConsole";
			
			if (args.length > 1) {
				for (int i = 1; i < args.length; i++)
					unmuteReason += " " + args[i];
			}
			String muteName = args[0];
			
			ProxiedPlayer mutePlayer = plugin.getProxy().getPlayer(muteName);
			UUID muteUUID;
			if(mutePlayer == null) {
				try {
					muteUUID = UUIDFetcher.getUUIDOf(muteName);
					if(muteUUID == null) {
						sender.sendMessage(new ComponentBuilder("Erreur: Ce joueur n'existe pas.").color(ChatColor.RED).create());
						return;
					}
				} catch (Exception e) {
					sender.sendMessage(new ComponentBuilder("Erreur lors de la récupération de l'UUID :").color(ChatColor.RED).append(e.getMessage()).color(ChatColor.GRAY).create());
					return;
				}
			} else {
				muteUUID = mutePlayer.getUniqueId();
			}
			
			Mute Mute = BungeeGuardUtils.getMute(muteUUID);
			if(Mute == null) {
				sender.sendMessage(new ComponentBuilder("Erreur: Ce joueur n'est pas mute.").color(ChatColor.RED).create());
			} else {
				Mute.removeFromBDD(unmuteReason, unmuteName);
				
				MuteType MuteTypeVar = (unmuteReason == "") ? MuteType.UNMUTE : MuteType.UNMUTE_W_REASON;
				String adminFormat = MuteTypeVar.adminFormat("", unmuteReason, unmuteName, muteName);
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

}
