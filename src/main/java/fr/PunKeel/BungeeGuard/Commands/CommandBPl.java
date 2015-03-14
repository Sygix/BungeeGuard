package fr.PunKeel.BungeeGuard.Commands;


import fr.PunKeel.BungeeGuard.Main;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.plugin.PluginDescription;
import net.md_5.bungee.api.plugin.PluginManager;

public class CommandBPl extends Command {
    private final Main plugin;

    public CommandBPl(Main plugin) {
        super("b:pl", "bungee.bpl", "b:up", "b:version", "b:ver");
        this.plugin = plugin;
    }

    private static String formatUptime(Long uptime) {
        String duration = "";

        int days = uptime.intValue() / (60 * 60 * 24);
        int hours = (uptime.intValue() / (60 * 60)) % 24;
        int minutes = (uptime.intValue() / 60) % 60;

        if (days != 0)
            duration += days + " jour" + s(days) + ", ";
        if (hours != 0)
            duration += hours + " heure" + s(hours) + ", ";
        if (minutes != 0)
            duration += minutes + " minute" + s(minutes);

        return duration;
    }

    private static String s(int n, String s, String none) {
        return (n > 1) ? s : none;
    }

    private static String s(int n) {
        return s(n, "s", "");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        sender.sendMessage(TextComponent.fromLegacyText(ChatColor.RED + "BungeeGuard"));
        sender.sendMessage(TextComponent.fromLegacyText(ChatColor.AQUA + "Uptime: " + ChatColor.RESET + formatUptime(plugin.getUptime())));

        sender.sendMessage(TextComponent.fromLegacyText("Liste des plugins:"));
        PluginManager PM = ProxyServer.getInstance().getPluginManager();

        for (Plugin p : PM.getPlugins()) {
            sender.sendMessage(new TextComponent(" "));
            PluginDescription d = p.getDescription();
            String coeur = d.getAuthor().equalsIgnoreCase("PunKeel") ? ChatColor.RED + "\u2764 " : ChatColor.GRAY + "";
            sender.sendMessage(TextComponent.fromLegacyText(ChatColor.GOLD + d.getName() + ChatColor.RESET + " par " + coeur + d.getAuthor()));
            sender.sendMessage(TextComponent.fromLegacyText(ChatColor.AQUA + "Version: " + ChatColor.RESET + d.getVersion()));
        }

    }
}
