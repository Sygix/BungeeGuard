package net.uhcwork.BungeeGuard.commands;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.uhcwork.BungeeGuard.BungeeGuardUtils;
import net.uhcwork.BungeeGuard.Main;
import net.uhcwork.BungeeGuard.MultiBungee.MultiBungee;

public class CommandGtp extends Command {
    public Main plugin;

    public CommandGtp(Main plugin) {
        super("gtp", "bungeeguard.gtp");
        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof ProxiedPlayer)) {
            return;
        }
        ProxiedPlayer p = (ProxiedPlayer) sender;

        if (args.length != 1) {
            BungeeGuardUtils.msgPluginCommand(sender);
            return;
        }
        MultiBungee MB = Main.getMB();
        if (MB.isPlayerOnline(args[0])) {
            ServerInfo server = MB.getServerFor(args[0]);
            sender.sendMessage(TextComponent.fromLegacyText(ChatColor.GREEN + "Téléportation vers " + ChatColor.BLUE + args[0] + ChatColor.GREEN + " dans le monde " + ChatColor.GOLD + Main.getPrettyServerName(server.getName()) + ChatColor.RESET + ChatColor.GOLD + "(" + server.getName() + ")" + ChatColor.GREEN + "..."));
            if (server.getName().equalsIgnoreCase(((ProxiedPlayer) sender).getServer().getInfo().getName())) {
                p.chat("/tp " + args[0]);
            } else {
                plugin.addGtp(p.getUniqueId(), args[0]);
                p.connect(server);
            }
        } else {
            sender.sendMessage(TextComponent.fromLegacyText(ChatColor.RED + "Erreur: Ce joueur n'est pas en ligne"));
        }
    }
}