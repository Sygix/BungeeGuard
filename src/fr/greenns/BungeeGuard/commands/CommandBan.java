package fr.greenns.BungeeGuard.commands;

import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import fr.greenns.BungeeGuard.BungeeGuard;
import fr.greenns.BungeeGuard.BungeeGuardUtils;
import fr.greenns.BungeeGuard.utils.Ban;
import fr.greenns.BungeeGuard.utils.BanType;
import fr.greenns.BungeeGuard.utils.UUIDFetcher;

public class CommandBan extends Command {

	public BungeeGuard plugin;
	Pattern timePattern = Pattern.compile("([0-9]+)([ywdhms])");

	public CommandBan(BungeeGuard plugin) {
		super("ban", "bungeeguard.ban");
		this.plugin = plugin;
	}

	// => /ban <pseudo> [raison]
	
	@Override
	public void execute(CommandSender sender, String[] args) {
		String adminName = sender.getName();
		String adminUUID = (sender instanceof ProxiedPlayer) ? ((ProxiedPlayer) sender).getUniqueId().toString() : "CONSOLE";

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
							case "y": bannedTime += number*365*24*60*60*1000; break;
							case "w": bannedTime += number*7*24*60*60*1000; break;
							case "d": bannedTime += number*24*60*60*1000; break;
							case "h": bannedTime += number*60*60*1000; break;
							case "m": bannedTime += number*60*1000; break;
							case "s": bannedTime += number*1000; break;
						}
					}
				}
				bannedUntilTime = System.currentTimeMillis() + bannedTime + 1;
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
			} else {
				BanTypeVar = (reason != null) ? BanType.PERMANENT_W_REASON : BanType.PERMANENT;
			}
			System.err.print(BanTypeVar.kickFormat("1000", "lol"));
			
			String bannedName = args[0];
			ProxiedPlayer bannedPlayer = plugin.getProxy().getPlayer(bannedName);
			UUID bannedUUID;
			String bannedDurationStr = (duration) ? BungeeGuardUtils.getDuration(bannedUntilTime) : "-1";
			if(bannedPlayer == null) {
				try {
					bannedUUID = UUIDFetcher.getUUIDOf(bannedName);
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
			
			BungeeGuard.bans.add(Ban);
			
			for(ProxiedPlayer p: plugin.getProxy().getPlayers()) {
				if(p.hasPermission("bungeeguard.notify")) p.sendMessage(new ComponentBuilder(BanTypeVar.adminFormat(bannedDurationStr, reason, adminName, bannedName)).create());
			}
		}
	}
}
