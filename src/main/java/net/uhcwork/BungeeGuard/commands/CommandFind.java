package net.uhcwork.BungeeGuard.commands;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.command.PlayerCommand;
import net.uhcwork.BungeeGuard.Main;

public class CommandFind extends PlayerCommand {
    Main plugin;

    public CommandFind(Main plugin) {
        super("find", "bungeecord.command.find");
        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length != 1) {
            sender.sendMessage(TextComponent.fromLegacyText(ChatColor.RED + "Please follow this command by a user name"));
        } else {
            ServerInfo SI = plugin.getMB().getServerFor(args[0]);
            String proxy = plugin.getMB().getProxy(args[0]);
            if (SI == null) {
                sender.sendMessage(TextComponent.fromLegacyText(ChatColor.RED + "That user is not online"));
                return;
            }
            sender.sendMessage(TextComponent.fromLegacyText(ChatColor.GRAY + "[" + ChatColor.AQUA + proxy + ChatColor.GRAY + "] " + ChatColor.GREEN + args[0] + ChatColor.GRAY + " est sur " + ChatColor.YELLOW + SI.getName()));
        }
    }
}