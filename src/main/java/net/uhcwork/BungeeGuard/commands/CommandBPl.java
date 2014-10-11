package net.uhcwork.BungeeGuard.Commands;


import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.plugin.PluginDescription;
import net.md_5.bungee.api.plugin.PluginManager;
import net.uhcwork.BungeeGuard.Main;

/**
 * Part of net.uhcwork.BungeeGuard.commands (bungeeguard)
 * Date: 27/09/2014
 * Time: 15:08
 * May be open-source & be sold (by mguerreiro, of course !)
 */
public class CommandBPl extends Command {
    public Main plugin;

    public CommandBPl(Main plugin) {
        super("b:pl", "bungeeguard.bpl", "b:up", "b:version", "b:ver");
        this.plugin = plugin;
    }

    private static String formatUptime(Long uptime) {
        String duration = "";

        int days = uptime.intValue() / (60 * 60 * 24);
        int hours = (uptime.intValue() / (60 * 60)) % 24;
        int minutes = (uptime.intValue() / 60) % 60;

        if (days != 0)
            duration += days + " jour" + ((days > 1) ? "s" : "") + ", ";
        if (hours != 0)
            duration += hours + "heure" + ((hours > 1) ? "s" : "") + ", ";
        if (minutes != 0)
            duration += minutes + "minute" + ((minutes > 1) ? "s" : "");

        return duration;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        sender.sendMessage(TextComponent.fromLegacyText(ChatColor.RED + "BungeeCord - UHCGames"));
        sender.sendMessage(TextComponent.fromLegacyText(ChatColor.AQUA + "Uptime: " + ChatColor.RESET + formatUptime(plugin.getUptime())));

        sender.sendMessage(TextComponent.fromLegacyText("Liste des plugins:"));
        PluginManager PM = ProxyServer.getInstance().getPluginManager();

        for (Plugin p : PM.getPlugins()) {
            sender.sendMessage(new TextComponent(" "));
            PluginDescription d = p.getDescription();
            String coeur = d.getAuthor().equalsIgnoreCase("PunKeel") ? ChatColor.RED + "\u2764 " : ChatColor.GRAY + " ";
            sender.sendMessage(TextComponent.fromLegacyText(ChatColor.GOLD + d.getName() + ChatColor.RESET + " par " + coeur + d.getAuthor()));
            sender.sendMessage(TextComponent.fromLegacyText(ChatColor.AQUA + "Version: " + ChatColor.RESET + d.getVersion()));
        }

    }
}
