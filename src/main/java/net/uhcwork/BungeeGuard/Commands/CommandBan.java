package net.uhcwork.BungeeGuard.Commands;

import com.google.common.base.Joiner;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.command.PlayerCommand;
import net.uhcwork.BungeeGuard.BungeeGuardUtils;
import net.uhcwork.BungeeGuard.Main;
import net.uhcwork.BungeeGuard.Managers.SanctionManager;
import net.uhcwork.BungeeGuard.Models.BungeeBan;
import net.uhcwork.BungeeGuard.MultiBungee.MultiBungee;
import net.uhcwork.BungeeGuard.Persistence.SaveRunner;

import java.net.InetAddress;
import java.util.Arrays;
import java.util.UUID;

public class CommandBan extends PlayerCommand {
    private final SanctionManager SM;
    private final Main plugin;
    private final MultiBungee MB;

    public CommandBan(Main plugin) {
        super("ban", "bungee.ban");
        SM = plugin.getSanctionManager();
        MB = Main.getMB();
        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        String adminName = (sender instanceof ProxiedPlayer) ? sender.getName() : "UHConsole";
        UUID adminUUID = (sender instanceof ProxiedPlayer) ? ((ProxiedPlayer) sender).getUniqueId() : UUID.fromString("00000000-0000-0000-0000-000000000000");

        if (args.length == 0) {
            sender.sendMessage(new ComponentBuilder("Usage: /ban <pseudo> [duration] [reason]").color(ChatColor.RED).create());
            return;
        }
        boolean duration = false;
        long bannedUntilTime;
        Long bannedTime = 0l;
        if (args.length > 1) {
            bannedTime = BungeeGuardUtils.parseDuration(args[1]);
            duration = (bannedTime != null && bannedTime > 0);
        }

        String reason = Joiner.on(" ").join(Arrays.copyOfRange(args, duration ? 2 : 1, args.length)).trim();

        if (!SM.isPremadeMessage(reason)) {
            if (sender.hasPermission("bungee.can.custom_sanction_message")) {
                sender.sendMessage(TextComponent.fromLegacyText(ChatColor.RED + "Raison invalide."));
                return;
            }
        } else {
            reason = SM.getPremadeMessage(reason);
        }
        reason = ChatColor.translateAlternateColorCodes('&', reason);

        String bannedName = args[0];
        UUID bannedUUID = MB.getUuidFromName(bannedName);
        if (bannedUUID == null) {
            sender.sendMessage(TextComponent.fromLegacyText(ChatColor.RED + "Joueur inexistant."));
            return;
        }

        long now = System.currentTimeMillis();
        bannedUntilTime = duration ? now + bannedTime : -1;
        InetAddress ipA = MB.getPlayerIp(bannedUUID);
        String ip = (ipA == null ? null : ipA.getHostAddress());
        BungeeBan ban = SM.ban(bannedUUID, bannedName, bannedUntilTime, reason, adminName, adminUUID, false);
        ban.setIp(ip);
        plugin.executePersistenceRunnable(new SaveRunner(ban));
        String adminNotification = ban.getAdminNotification(now);

        MB.kickPlayer(bannedName, ban.getBanMessage(now));

        MB.banPlayer(bannedUUID, bannedName, bannedUntilTime, reason, adminName, adminUUID);
        MB.notifyStaff(adminNotification);
    }
}
