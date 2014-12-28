package net.uhcwork.BungeeGuard.Commands;

import com.google.common.base.Joiner;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.uhcwork.BungeeGuard.BungeeGuardUtils;
import net.uhcwork.BungeeGuard.Main;
import net.uhcwork.BungeeGuard.Managers.SanctionManager;
import net.uhcwork.BungeeGuard.Models.BungeeMute;
import net.uhcwork.BungeeGuard.MultiBungee.MultiBungee;

import java.util.Arrays;
import java.util.UUID;

public class CommandMute extends Command {
    private final SanctionManager SM;
    private final MultiBungee MB;

    public CommandMute(Main plugin) {
        super("mute", "bungee.mute");
        this.SM = plugin.getSanctionManager();
        MB = Main.getMB();
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        String adminName = (sender instanceof ProxiedPlayer) ? sender.getName() : "UHConsole";
        UUID adminUUID = (sender instanceof ProxiedPlayer) ? ((ProxiedPlayer) sender).getUniqueId() : UUID.fromString("00000000-0000-0000-0000-000000000000");

        if (args.length == 0) {
            sender.sendMessage(new ComponentBuilder("Usage: /mute <pseudo> [duration] [reason]").color(ChatColor.RED).create());
            return;
        }
        boolean duration = true;
        Long muteTime = 0l;
        if (args.length > 1) {
            muteTime = BungeeGuardUtils.parseDuration(args[1]);
            duration = (muteTime != null && muteTime > 0);
        }
        if (!duration || muteTime > 604800000L || muteTime < 1)
            muteTime = 604800000L;


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

        String muteName = args[0];
        long now = System.currentTimeMillis();

        UUID muteUUID = Main.getMB().getUuidFromName(muteName);
        if (muteUUID == null) {
            sender.sendMessage(TextComponent.fromLegacyText(ChatColor.RED + "Joueur inexistant."));
            return;
        }

        long muteUntilTime = now + muteTime;

        BungeeMute mute = SM.mute(muteUUID, muteName, muteUntilTime, reason, adminName, adminUUID, true);
        String adminNotification = mute.getAdminNotification(now);

        MB.sendPlayerMessage(muteUUID, mute.getMuteMessage(now));

        MB.mutePlayer(muteUUID, muteName, muteUntilTime, reason, adminName, adminUUID);

        MB.notifyStaff(adminNotification);
    }
}
