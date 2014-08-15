package fr.greenns.BungeeGuard.Mute;

import java.util.regex.Matcher;

import net.md_5.bungee.api.ChatColor;

public enum MuteType {
	NON_PERMANENT(ChatColor.RED + "Vous avez été mute pendant " + ChatColor.AQUA + "+timeStr" + ChatColor.RED + ".", ChatColor.AQUA + "+adminName" + ChatColor.RED + " a mute " + ChatColor.GREEN + "+muteName" + ChatColor.RED + " pendant " + ChatColor.AQUA + "+timeStr" + ChatColor.RED + "."),
	NON_PERMANENT_W_REASON(ChatColor.RED + "Vous avez été mute pendant " + ChatColor.AQUA + "+timeStr" + ChatColor.RED + " pour: "+'\n'+"+reasonStr", ChatColor.AQUA + "+adminName" + ChatColor.RED +" a mute " +ChatColor.GREEN+ "+muteName" + ChatColor.RED + " pendant "+ChatColor.AQUA+"+timeStr" + ChatColor.RED + " pour: "+ ChatColor.AQUA + "+reasonStr"),
	UNMUTE("", ChatColor.AQUA + "+adminName" + ChatColor.RED + " a démute " + ChatColor.GREEN + "+muteName" + ChatColor.RED + "."),
	UNMUTE_W_REASON("", ChatColor.AQUA + "+adminName" + ChatColor.RED + " a démute " + ChatColor.GREEN + "+muteName" + ChatColor.RED + " avec la raison: " + ChatColor.AQUA + "+reasonStr" + ChatColor.RED + ".");
	
	String kickFormat;
	String adminFormat;
	String adminPrefix = ChatColor.RED+"[BungeeGuard] ";
	
	private MuteType(String kickFormat, String adminFormat) {
		this.kickFormat = kickFormat;
		this.adminFormat = adminFormat;
	}
	
	public String playerFormat(String timeStr, String reasonStr) {
		String message = kickFormat;
		if(message.contains("+timeStr")) {
			timeStr = Matcher.quoteReplacement(timeStr);
			message = message.replaceAll("\\+timeStr", timeStr);
		} 
		if(message.contains("+reasonStr")) {
			reasonStr = Matcher.quoteReplacement(reasonStr);
			message = message.replaceAll("\\+reasonStr", reasonStr);
		}
		return message;
	}
	
	public String adminFormat(String timeStr, String reasonStr, String adminName, String muteName) {
		String message = adminFormat;
		if(message.contains("+timeStr")) {
			timeStr = Matcher.quoteReplacement(timeStr);
			message = message.replaceAll("\\+timeStr", timeStr);
		} 
		if(message.contains("+reasonStr")) {
			reasonStr = Matcher.quoteReplacement(reasonStr);
			message = message.replaceAll("\\+reasonStr", reasonStr);
		}
		if(message.contains("+adminName")) {
			adminName = Matcher.quoteReplacement(adminName);
			message = message.replaceAll("\\+adminName", adminName);
		}
		if(message.contains("+muteName")) {
			muteName = Matcher.quoteReplacement(muteName);
			message = message.replaceAll("\\+muteName", muteName);
		}
		return adminPrefix+message;
	}
}
