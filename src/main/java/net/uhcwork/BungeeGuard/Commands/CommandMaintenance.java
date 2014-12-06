package net.uhcwork.BungeeGuard.Commands;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Command;
import net.uhcwork.BungeeGuard.Main;
import net.uhcwork.BungeeGuard.Models.BungeeServer;
import net.uhcwork.BungeeGuard.MultiBungee.MultiBungee;
import net.uhcwork.BungeeGuard.Permissions.Permissions;
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
        boolean notFound = true;
        String pattern = args[0];
        MultiBungee MB = Main.getMB();
        for (String name : ProxyServer.getInstance().getServers().keySet()) {
            if (!Permissions.miniglob(pattern, name))
                continue;

            boolean isRestricted = !plugin.isRestricted(name);
            MB.setMaintenance(name, isRestricted);
            if (isRestricted) {
                sender.sendMessage(TextComponent.fromLegacyText(ChatColor.RED + "Maintenance activée pour " + Main.getPrettyServerName(name)));
            } else {
                sender.sendMessage(TextComponent.fromLegacyText(ChatColor.GREEN + "Maintenance désactivée pour " + Main.getPrettyServerName(name)));
            }
            BungeeServer server = plugin.getServerManager().getServerModel(name);
            server.setRestricted(isRestricted);
            plugin.executePersistenceRunnable(new SaveRunner(server));
            notFound = false;
        }
        if (notFound) {
            sender.sendMessage(TextComponent.fromLegacyText(ChatColor.RED + "Serveur ... inconnu u.u"));
        }
    }
}