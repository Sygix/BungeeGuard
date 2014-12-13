package net.uhcwork.BungeeGuard.Commands;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.plugin.Command;
import net.uhcwork.BungeeGuard.BungeeGuardUtils;
import net.uhcwork.BungeeGuard.Main;
import net.uhcwork.BungeeGuard.Models.BungeeBan;

import java.util.UUID;

public class CommandCheck extends Command {

    private final Main plugin;

    public CommandCheck(Main plugin) {
        super("check", "bungee.check");
        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length != 1) {
            sender.sendMessage(new ComponentBuilder("Usage: /check <player>").color(ChatColor.RED).create());
            return;
        }
        UUID bannedUUID = Main.getMB().getUuidFromName(args[0]);
        BungeeBan ban = plugin.getSanctionManager().findBan(bannedUUID);

        if (ban == null) {
            sender.sendMessage(new ComponentBuilder("Le joueur ").color(ChatColor.YELLOW).append(args[0]).color(ChatColor.AQUA).append(" n'est pas banni.").color(ChatColor.YELLOW).create());
            return;
        }
        sender.sendMessage(new ComponentBuilder("Le joueur ").color(ChatColor.YELLOW).append(args[0]).color(ChatColor.AQUA).append(" est banni.").color(ChatColor.YELLOW).create());
        if (!ban.getReason().isEmpty())
            sender.sendMessage(new ComponentBuilder("Raison: ").color(ChatColor.YELLOW).append(ban.getReason()).color(ChatColor.AQUA).create());
        if (ban.getUntilTimestamp() != -1)
            sender.sendMessage(new ComponentBuilder("Pendant encore ").color(ChatColor.YELLOW).append(BungeeGuardUtils.getDuration(ban.getUntilTimestamp())).color(ChatColor.AQUA).create());
        sender.sendMessage(new ComponentBuilder("Par:  ").color(ChatColor.YELLOW).append(ban.getAdminName()).color(ChatColor.AQUA).create());
    }
}
