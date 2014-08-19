package fr.greenns.BungeeGuard.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;

public class ComponentManager {
	static String formatPatternStr = "§([0-9a-fA-Fk-or])";
	static Pattern formatPattern = Pattern.compile(formatPatternStr);
	
	public static BaseComponent[] generate(String message) {
		ComponentBuilder component = new ComponentBuilder("");
		Matcher m = formatPattern.matcher(message);
		
		int startPos = 0;
		ChatColor savedColor = null;
		while (m.find()) {
			String string = message.substring(startPos, m.start());
			if(string != "") {
				component.append(string);
				if(savedColor != null) component = format(savedColor, component);
			} else {
				if(savedColor != null) component = format(savedColor, component);
			}
			
			startPos = m.end();
			savedColor = ChatColor.getByChar(m.group(1).toCharArray()[0]);
		}
		
		if(startPos < message.length()) {
			String string = message.substring(startPos, message.length());
			component.append(string);
			if(savedColor != null) component = format(savedColor, component);
		}
		
		return component.create();
	}
	
	private static ComponentBuilder format(ChatColor color, ComponentBuilder component) {
		if(color.equals(ChatColor.BOLD)) component.bold(true);
		else if(color.equals(ChatColor.ITALIC)) component.italic(true);
		else if(color.equals(ChatColor.STRIKETHROUGH)) component.strikethrough(true);
		else if(color.equals(ChatColor.UNDERLINE)) component.underlined(true);
		else component.color(color);
		
		return component;
	}
}
