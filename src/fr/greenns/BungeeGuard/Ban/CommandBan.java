package fr.greenns.BungeeGuard.Ban;

import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import fr.greenns.BungeeGuard.BungeeGuard;
import fr.greenns.BungeeGuard.BungeeGuardUtils;
import fr.greenns.BungeeGuard.utils.UUIDFetcher;

public class CommandBan extends Command {

	public BungeeGuard plugin;
	Pattern timePattern = Pattern.compile("([0-9]+)(mo|[ywdhms])");

	public CommandBan(BungeeGuard plugin) {
		super("ban", "bungeeguard.ban");
		this.plugin = plugin;
	}
	
	@Override
	public void execute(CommandSender sender, String[] args) {
		long startTime = System.currentTimeMillis();
		String adminName = (sender instanceof ProxiedPlayer) ? sender.getName() : "UHConsole";
		String adminUUID = (sender instanceof ProxiedPlayer) ? ((ProxiedPlayer) sender).getUniqueId().toString() : "UHConsole";

		if (args.length == 0) {
			sender.sendMessage(new ComponentBuilder("Usage: /ban <pseudo> [duration] [reason]").color(ChatColor.RED).create());
		} else {
			boolean duration = false;
			long bannedUntilTime = -1;
			if(args.length > 1) {
				Matcher m = timePattern.matcher(args[1]);
				long bannedTime = 0;
				while (m.find()) {
					if (m.group() == null || m.group().isEmpty()) {
						continue;
					} else if(m.group(1) != null && !m.group(1).isEmpty() && m.group(2) != null && !m.group(2).isEmpty()) {
						int number = Integer.parseInt(m.group(1));
						String type = m.group(2);
						duration = true;
						
						switch(type) {
							case "y": bannedTime += number*31536000000L; break;
							case "mo": bannedTime += number*2592000000L; break;
							case "w": bannedTime += number*604800000L; break;
							case "d": bannedTime += number*86400000L; break;
							case "h": bannedTime += number*3600000L; break;
							case "m": bannedTime += number*60000L; break;
							case "s": bannedTime += number*1000L; break;
						}
					}
				}
				bannedUntilTime = System.currentTimeMillis() + bannedTime;
			}
			
			int startArgForReason = (duration) ? 2 : 1; 
			
			String reason = "";
			if(args.length > startArgForReason) {
				for (int i = startArgForReason; i < args.length; i++){
					reason += " " + args[i];
				}
			}
			if(reason == "") reason = null;
			
			BanType BanTypeVar;
			if(duration) {
				BanTypeVar = (reason != null) ? BanType.NON_PERMANENT_W_REASON : BanType.NON_PERMANENT;
				bannedUntilTime += (System.currentTimeMillis() - startTime);
			} else {
				BanTypeVar = (reason != null) ? BanType.PERMANENT_W_REASON : BanType.PERMANENT;
				bannedUntilTime = -1;
			}
			
			String bannedName = args[0];
			ProxiedPlayer bannedPlayer = plugin.getProxy().getPlayer(bannedName);
			UUID bannedUUID;
			String bannedDurationStr = BungeeGuardUtils.getDuration(bannedUntilTime);
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
				bannedPlayer.disconnect(new ComponentBuilder(BanTypeVar.kickFormat(bannedDurationStr, reason)).create());
			}
			
			Ban alreadyBan = BungeeGuardUtils.getBan(bannedUUID);
			if(alreadyBan != null) BungeeGuard.bans.remove(alreadyBan);

			Ban Ban = new Ban(bannedUUID, bannedName, bannedUntilTime, reason, adminName, adminUUID);
			Ban.addToBdd();
			
			String adminFormat = BanTypeVar.adminFormat(bannedDurationStr, reason, adminName, bannedName);
			BaseComponent[] message = new ComponentBuilder(adminFormat).create();			
			for(ProxiedPlayer p: plugin.getProxy().getPlayers()) {
				if(p.hasPermission("bungeeguard.notify")) {
					p.sendMessage(message);
				}
			}
			System.out.print(adminFormat);
		}
	}
}
