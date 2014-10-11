package net.uhcwork.BungeeGuard.Commands;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.uhcwork.BungeeGuard.Ban.BanManager;
import net.uhcwork.BungeeGuard.Ban.BanType;
import net.uhcwork.BungeeGuard.BungeeGuardUtils;
import net.uhcwork.BungeeGuard.Main;
import net.uhcwork.BungeeGuard.Models.BungeeBan;

import java.util.UUID;

public class CommandBan extends Command {

    public Main plugin;
    BanManager BM;

    public CommandBan(Main plugin) {
        super("ban", "bungeeguard.ban");
        this.plugin = plugin;
        this.BM = plugin.getBM();
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        String adminName = (sender instanceof ProxiedPlayer) ? sender.getName() : "UHConsole";
        UUID adminUUID = (sender instanceof ProxiedPlayer) ? ((ProxiedPlayer) sender).getUniqueId() : UUID.fromString("00000000-0000-0000-0000-000000000000");

        if (args.length == 0) {
            sender.sendMessage(new ComponentBuilder("Usage: /ban <pseudo> [duration] [reason]").color(ChatColor.RED).create());
        } else {
            boolean duration = false;
            long bannedUntilTime;
            Long bannedTime = 0l;
            if (args.length > 1) {
                bannedTime = BungeeGuardUtils.parseDuration(args[1]);
                duration = (bannedTime != null && bannedTime > 0);
            }

            int startArgForReason = (duration) ? 2 : 1;

            String reason = "";
            if (args.length > startArgForReason) {
                for (int i = startArgForReason; i < args.length; i++) {
                    reason += " " + args[i];
                }
            }
            reason = reason.trim();

            if (plugin.isPremadeMessage(reason))
                reason = plugin.getPremadeMessage(reason);
            if (!reason.isEmpty())
                reason = ChatColor.translateAlternateColorCodes('&', reason);

            BanType BanTypeVar;
            if (duration) {
                BanTypeVar = (reason != null) ? BanType.NON_PERMANENT_W_REASON : BanType.NON_PERMANENT;
                bannedUntilTime = System.currentTimeMillis() + bannedTime + 1; // Une seconde de ban gratuite :D
            } else {
                BanTypeVar = (reason != null) ? BanType.PERMANENT_W_REASON : BanType.PERMANENT;
                bannedUntilTime = -1;
            }

            String bannedName = args[0];
            UUID bannedUUID = Main.getMB().getUuidFromName(bannedName);
            if (bannedUUID == null) {
                sender.sendMessage(TextComponent.fromLegacyText(ChatColor.RED + "Joueur inexistant."));
                return;
            }
            String bannedDurationStr = BungeeGuardUtils.getDuration(bannedUntilTime);

            Main.getMB().kickPlayer(bannedName, BanTypeVar.kickFormat(bannedDurationStr, reason));

            BungeeBan ban = BM.ban(bannedUUID, bannedName, bannedUntilTime, reason, adminName, adminUUID, true);
            ban.saveIt();

            Main.getMB().banPlayer(bannedUUID, bannedName, bannedUntilTime, reason, adminName, adminUUID);

            String adminFormat = BanTypeVar.adminFormat(bannedDurationStr, reason, adminName, bannedName);
            Main.getMB().notifyStaff(adminFormat);
        }
    }
}