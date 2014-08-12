package fr.greenns.BungeeGuard.utils;

import net.md_5.bungee.api.ChatColor;

public enum KickType {
	
	
	KICK(ChatColor.RED + "Vous avez été kické du serveur.", ChatColor.AQUA + "+adminName" + ChatColor.RED + " a kické " + ChatColor.GREEN + "+kickedName" + ChatColor.RED + " du serveur."),
	KICK_W_REASON(ChatColor.RED + "Vous avez été kické du serveur pour:"+ '\n' + ChatColor.AQUA + "+reasonStr", ChatColor.AQUA + "+adminName" + ChatColor.RED + " a kické " + ChatColor.GREEN + "+kickedName" + ChatColor.RED + " pour: " + ChatColor.AQUA + "+reasonStr" + ChatColor.RED + ".");
	
	String kickFormat;
	String adminFormat;
	private KickType(String kickFormat, String adminFormat) {
		this.kickFormat = kickFormat;
		this.adminFormat = adminFormat;
	}
	
	public String kickFormat(String reasonStr) {
		String message = kickFormat;
		if(message.contains("+reasonStr")) {
			message = message.replaceAll("\\+reasonStr", reasonStr);
		}
		return message;
	}
	
	public String adminFormat(String reasonStr, String adminName, String kickedName) {
		String message = adminFormat;
		if(message.contains("+reasonStr")) {
			message = message.replaceAll("\\+reasonStr", reasonStr);
		}
		if(message.contains("+adminName")) {
			message = message.replaceAll("\\+adminName", adminName);
		}
		if(message.contains("+kickedName")) {
			message = message.replaceAll("\\+kickedName", kickedName);
		}
		return message;
	}
}
