package net.uhcwork.BungeeGuard.Commands;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Command;
import net.uhcwork.BungeeGuard.Main;
import net.uhcwork.BungeeGuard.Models.BungeeServer;
import net.uhcwork.BungeeGuard.MultiBungee.MultiBungee;
import net.uhcwork.BungeeGuard.Persistence.SaveRunner;

public class CommandMaintenance extends Command {
    private final Main plugin;

    public CommandMaintenance(Main plugin) {
        super("maintenance", "bungee.maintenance");
        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length != 1) {
            sender.sendMessage(TextComponent.fromLegacyText(ChatColor.RED + "Usage: /maintenance <serveur_name>"));
            return;
        }
        String serverName = args[0];
        MultiBungee MB = Main.getMB();
        if (plugin.getProxy().getServerInfo(serverName) == null) {
            sender.sendMessage(TextComponent.fromLegacyText(ChatColor.RED + "Serveur ... inconnu u.u"));
            return;
        }
        boolean isRestricted = !plugin.isRestricted(serverName);
        MB.setMaintenance(serverName, isRestricted);
        if (isRestricted) {
            sender.sendMessage(TextComponent.fromLegacyText(ChatColor.DARK_RED + "Maintenance activée pour " + Main.getPrettyServerName(serverName)));
        } else {
            sender.sendMessage(TextComponent.fromLegacyText(ChatColor.GREEN + "Maintenance désactivée pour " + Main.getPrettyServerName(serverName)));
        }
        BungeeServer server = plugin.getServerManager().getServerModel(serverName);
        server.setRestricted(isRestricted);
        plugin.executePersistenceRunnable(new SaveRunner(server));
    }
}