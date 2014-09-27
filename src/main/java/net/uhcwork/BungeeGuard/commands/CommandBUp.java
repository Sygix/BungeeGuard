package net.uhcwork.BungeeGuard.commands;


import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Command;
import net.uhcwork.BungeeGuard.Main;

/**
 * Part of net.uhcwork.BungeeGuard.commands (bungeeguard)
 * Date: 27/09/2014
 * Time: 15:08
 * May be open-source & be sold (by mguerreiro, of course !)
 */
public class CommandBUp extends Command {
    public Main plugin;

    public CommandBUp(Main plugin) {
        super("b:ver", "bungeeguard.bup", "b:up", "b:version");
        this.plugin = plugin;
    }

    private static String formatUptime(Number uptime) {
        String retval = "";

        int days = (int) uptime / (60 * 60 * 24);
        int minutes, hours;

        if (days != 0)
            retval += days + " jour" + ((days > 1) ? "s" : "") + ", ";

        minutes = (int) uptime / 60;
        hours = minutes / 60;
        hours %= 24;
        minutes %= 60;

        if (hours != 0)
            retval += hours + ":" + minutes;
        else
            retval += minutes + " min";

        return retval;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        sender.sendMessage(TextComponent.fromLegacyText(":" + ChatColor.AQUA + plugin.getDescription().getName() + ChatColor.RESET + ": par " + ChatColor.RED + "\u2665 " + plugin.getDescription().getAuthor()));
        sender.sendMessage(TextComponent.fromLegacyText(ChatColor.AQUA + "Version: " + ChatColor.RESET + plugin.getDescription().getVersion()));
        sender.sendMessage(TextComponent.fromLegacyText(ChatColor.AQUA + "Uptime: " + ChatColor.RESET + formatUptime(plugin.getUptime())));
    }
}
