package net.uhcwork.BungeeGuard.commands;

/**
 * Part of ${PACKAGE_NAME} (${PROJECT_NAME})
 * Date: 07/09/2014
 * Time: 18:45
 * May be open-source & be sold (by mguerreiro, of course !)
 */

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Command;
import net.uhcwork.BungeeGuard.Main;

public class CommandBLoad extends Command {
    public Main plugin;

    public CommandBLoad(Main plugin) {
        super("b:load", "bungeeguard.bload");
        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        for (String proxy : plugin.getMB().getAllServers()) {
            sender.sendMessage(new TextComponent(ChatColor.BLUE + proxy + ChatColor.RESET + ": " + ChatColor.GREEN + plugin.getMB().getPlayersOnProxy(proxy) + ChatColor.RESET + " joueur(s)"));
        }
    }
}