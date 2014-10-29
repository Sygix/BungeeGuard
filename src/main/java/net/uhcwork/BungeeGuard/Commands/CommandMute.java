package net.uhcwork.BungeeGuard.Commands;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.uhcwork.BungeeGuard.BungeeGuardUtils;
import net.uhcwork.BungeeGuard.Main;
import net.uhcwork.BungeeGuard.Managers.MuteManager;
import net.uhcwork.BungeeGuard.Models.BungeeMute;

import java.util.UUID;

public class CommandMute extends Command {

    public Main plugin;
    private MuteManager MM;

    public CommandMute(Main plugin) {
        super("mute", "bungee.mute");
        this.plugin = plugin;
        this.MM = plugin.getMuteManager();
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

        long muteUntilTime = System.currentTimeMillis() + muteTime + 1; // 1 seconde de mute gratuite !

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

        if (reason != null)
            reason = ChatColor.translateAlternateColorCodes('&', reason);

        String muteName = args[0];

        UUID muteUUID = Main.getMB().getUuidFromName(muteName);
        if (muteUUID == null) {
            sender.sendMessage(TextComponent.fromLegacyText(ChatColor.RED + "Joueur inexistant."));
            return;
        }
        BungeeMute mute = MM.mute(muteUUID, muteName, muteUntilTime, reason, adminName, adminUUID, true);

        Main.getMB().sendPlayerMessage(muteUUID, mute.getMuteMessage());

        Main.getMB().mutePlayer(muteUUID, muteName, muteUntilTime, reason, adminName, adminUUID);

        Main.getMB().notifyStaff(mute.getAdminNotification());
    }
}
