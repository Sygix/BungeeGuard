package net.uhcwork.BungeeGuard.Commands;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.uhcwork.BungeeGuard.Main;
import net.uhcwork.BungeeGuard.MultiBungee.MultiBungee;

import java.util.UUID;


public class CommandGtpHere extends Command {
    private final Main plugin;

    public CommandGtpHere(Main plugin) {
        super("gtphere", "bungee.gtphere", "gs");
        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof ProxiedPlayer)) {
            return;
        }
        ProxiedPlayer p = (ProxiedPlayer) sender;

        if (args.length != 1) {
            sender.sendMessage(TextComponent.fromLegacyText(ChatColor.RED + "Usage: /gs <pseudo>"));
            return;
        }
        MultiBungee MB = Main.getMB();
        String playerName = args[0];
        UUID u = MB.getUuidFromName(playerName);
        if (u == null || MB.isPlayerOnline(playerName)) {
            ServerInfo server = MB.getServerFor(playerName);
            sender.sendMessage(TextComponent.fromLegacyText(ChatColor.GREEN + "Envoi de " + playerName + " vers vous ..."));
            if (server.getName().equalsIgnoreCase(((ProxiedPlayer) sender).getServer().getInfo().getName())) {
                p.chat("/tp " + args[0] + " " + p.getName());
            } else {
                MB.gtp(playerName, sender.getName());
            }
        } else {
            sender.sendMessage(TextComponent.fromLegacyText(ChatColor.RED + "Erreur: Ce joueur n'est pas en ligne"));
        }
    }
}