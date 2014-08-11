package fr.greenns.BungeeGuard.utils;

import net.md_5.bungee.api.ChatColor;

public enum BanType {
	PERMANENT(ChatColor.RED + "Vous avez été banni définitivement.", ChatColor.AQUA + "+adminName" + ChatColor.RED + " a banni " + ChatColor.GREEN + "+bannedName" + ChatColor.RED + " définitivement."),
	NON_PERMANENT(ChatColor.RED + "Vous avez été banni pendant " + ChatColor.AQUA + "+timeStr" + ChatColor.RED + ".", ChatColor.AQUA + "+adminName" + ChatColor.RED + " a banni " + ChatColor.GREEN + "+bannedName" + ChatColor.RED + " pendant " + ChatColor.AQUA + "+timeStr" + ChatColor.RED + "."),
	PERMANENT_W_REASON(ChatColor.RED + "Vous avez été banni définitivement pour:"+ '\n' + ChatColor.AQUA + "+reasonStr", ChatColor.AQUA + "+adminName" + ChatColor.RED + " a banni " + ChatColor.GREEN + "+bannedName" + ChatColor.RED + " définitivement pour:" + ChatColor.RED + " pour: " + ChatColor.AQUA + "+reasonStr" + ChatColor.RED + "."),
	NON_PERMANENT_W_REASON(ChatColor.RED + "Vous avez été banni pendant " + ChatColor.AQUA + "+timeStr" + ChatColor.RED + " pour: "+'\n'+"+reasonStr", ChatColor.AQUA + "+adminName" + ChatColor.RED +" a banni " +ChatColor.GREEN+ "+bannedName" + ChatColor.RED + "pendant "+ChatColor.AQUA+"+timeStr" + ChatColor.RED + " pour: "+'\n'+ ChatColor.AQUA + "+reasonStr");
	
	String kickFormat;
	String adminFormat;
	private BanType(String kickFormat, String adminFormat) {
		this.kickFormat = kickFormat;
		this.adminFormat = adminFormat;
	}
	
	public String kickFormat(String timeStr, String reasonStr) {
		String message = kickFormat;
		if(message.contains("+timeStr")) {
			message = message.replaceAll("\\+timeStr", timeStr);
		} 
		if(message.contains("+reasonStr")) {
			message = message.replaceAll("\\+reasonStr", reasonStr);
		}
		return message;
	}
	
	public String adminFormat(String timeStr, String reasonStr, String adminName, String bannedName) {
		String message = adminFormat;
		if(message.contains("+timeStr")) {
			message = message.replaceAll("\\+timeStr", timeStr);
		} 
		if(message.contains("+reasonStr")) {
			message = message.replaceAll("\\+reasonStr", reasonStr);
		}
		if(message.contains("+adminName")) {
			message = message.replaceAll("\\+adminName", adminName);
		}
		if(message.contains("+bannedName")) {
			message = message.replaceAll("\\+bannedName", bannedName);
		}
		return message;
	}
}
