package fr.greenns.BungeeGuard.Ban;

import fr.greenns.BungeeGuard.BungeeGuardUtils;
import fr.greenns.BungeeGuard.Main;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import java.util.UUID;
import java.util.regex.Pattern;

public class CommandBan extends Command {

    public Main plugin;
    Pattern timePattern = Pattern.compile("([0-9]+)(mo|[ywdhms])");

    public CommandBan(Main plugin) {
        super("ban", "bungeeguard.ban");
        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        long startTime = System.currentTimeMillis();
        String adminName = (sender instanceof ProxiedPlayer) ? sender.getName() : "UHConsole";
        UUID adminUUID = (sender instanceof ProxiedPlayer) ? ((ProxiedPlayer) sender).getUniqueId() : UUID.fromString("UHConsole");

        if (args.length == 0) {
            sender.sendMessage(new ComponentBuilder("Usage: /ban <pseudo> [duration] [reason]").color(ChatColor.RED).create());
        } else {
            boolean duration = false;
            long bannedUntilTime = -1;
            long bannedTime = 0;
            if (args.length > 1) {
                bannedTime = BungeeGuardUtils.parseDuration(args[1]);
                if (bannedTime > 0)
                    duration = true;
            }

            int startArgForReason = (duration) ? 2 : 1;

            String reason = "";
            if (args.length > startArgForReason) {
                for (int i = startArgForReason; i < args.length; i++) {
                    reason += " " + args[i];
                }
            }
            if (reason.equals("")) reason = null;
            if (reason != null) reason = ChatColor.translateAlternateColorCodes('&', reason);

            BanType BanTypeVar;
            if (duration) {
                BanTypeVar = (reason != null) ? BanType.NON_PERMANENT_W_REASON : BanType.NON_PERMANENT;
                bannedUntilTime = System.currentTimeMillis() + bannedTime + 1; // Une seconde de ban gratuite :D
            } else {
                BanTypeVar = (reason != null) ? BanType.PERMANENT_W_REASON : BanType.PERMANENT;
                bannedUntilTime = -1;
            }

            String bannedName = args[0];
            UUID bannedUUID = BungeeGuardUtils.getMB().getUuidFromName(bannedName);
            String bannedDurationStr = BungeeGuardUtils.getDuration(bannedUntilTime);

            BungeeGuardUtils.getMB().kickPlayer(bannedName, BanTypeVar.kickFormat(bannedDurationStr, reason));


            Ban alreadyBan = BungeeGuardUtils.getBan(bannedUUID);
            if (alreadyBan != null)
                Main.bans.remove(alreadyBan);

            Ban Ban = new Ban(bannedUUID, bannedName, bannedUntilTime, reason, adminName, adminUUID);
            Ban.addToBdd();
            BungeeGuardUtils.getMB().banPlayer(bannedUUID, bannedName, bannedUntilTime, reason, adminName, adminUUID);

            String adminFormat = BanTypeVar.adminFormat(bannedDurationStr, reason, adminName, bannedName);
            BungeeGuardUtils.getMB().notifyStaff(adminFormat);
        }
    }
}
