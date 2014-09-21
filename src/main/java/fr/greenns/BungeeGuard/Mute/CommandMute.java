package fr.greenns.BungeeGuard.Mute;

import fr.greenns.BungeeGuard.BungeeGuardUtils;
import fr.greenns.BungeeGuard.Main;
import fr.greenns.BungeeGuard.MultiBungee.MultiBungee;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import java.util.UUID;

public class CommandMute extends Command {

    private MuteManager MM;
    public Main plugin;

    public CommandMute(Main plugin) {
        super("mute", "bungeeguard.mute");
        this.plugin = plugin;
        this.MM = plugin.getMM();
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        long startTime = System.currentTimeMillis();
        String adminName = (sender instanceof ProxiedPlayer) ? sender.getName() : "UHConsole";
        UUID adminUUID = (sender instanceof ProxiedPlayer) ? ((ProxiedPlayer) sender).getUniqueId() : UUID.fromString("UHConsole");

        if (args.length == 0) {
            sender.sendMessage(new ComponentBuilder("Usage: /mute <pseudo> [duration] [reason]").color(ChatColor.RED).create());
        } else {
            boolean duration = true;
            long muteTime = 0;
            if (args.length > 1) {
                muteTime = BungeeGuardUtils.parseDuration(args[1]);
                if (muteTime <= 0)
                    duration = false;
            }
            if (muteTime > 604800000L) muteTime = 604800000L;
            if (!duration) muteTime = 604800000L;
            long muteUntilTime = System.currentTimeMillis() + muteTime + 1; // 1 seconde de mute gratuite !

            int startArgForReason = (duration) ? 2 : 1;

            String reason = "";
            if (args.length > startArgForReason) {
                for (int i = startArgForReason; i < args.length; i++) {
                    reason += " " + args[i];
                }
            }
            if (reason.equals("")) reason = null;

            MuteType MuteTypeVar;
            MuteTypeVar = (reason != null) ? MuteType.NON_PERMANENT_W_REASON : MuteType.NON_PERMANENT;
            if (reason != null) reason = ChatColor.translateAlternateColorCodes('&', reason);

            String muteName = args[0];

            UUID muteUUID = BungeeGuardUtils.getMB().getUuidFromName(muteName);
            String muteDurationStr = BungeeGuardUtils.getDuration(muteUntilTime);
            String muteMessage = MuteTypeVar.playerFormat(muteDurationStr, reason);

            BungeeGuardUtils.getMB().sendPlayerMessage(muteUUID, muteMessage);


            MM.mute(muteUUID, muteName, muteUntilTime, reason, adminName, adminUUID, true);

            BungeeGuardUtils.getMB().sendChannelMessage("mute",
                    BungeeGuardUtils.getMB().getServerId() + MultiBungee.SEPARATOR + muteUUID + MultiBungee.SEPARATOR + muteName + MultiBungee.SEPARATOR + muteUntilTime + MultiBungee.SEPARATOR +
                            reason + MultiBungee.SEPARATOR + adminName + MultiBungee.SEPARATOR + adminUUID);

            String adminFormat = MuteTypeVar.adminFormat(muteDurationStr, reason, adminName, muteName);
            BungeeGuardUtils.getMB().notifyStaff(adminFormat);
        }
    }
}
