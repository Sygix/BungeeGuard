package fr.greenns.BungeeGuard.Mute;

import fr.greenns.BungeeGuard.BungeeGuardUtils;
import fr.greenns.BungeeGuard.Main;
import fr.greenns.BungeeGuard.utils.MultiBungee;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommandMute extends Command {

    public Main plugin;
    Pattern timePattern = Pattern.compile("([0-9]+)([wdhms])");

    public CommandMute(Main plugin) {
        super("mute", "bungeeguard.mute");
        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        long startTime = System.currentTimeMillis();
        String adminName = (sender instanceof ProxiedPlayer) ? sender.getName() : "UHConsole";
        String adminUUID = (sender instanceof ProxiedPlayer) ? ((ProxiedPlayer) sender).getUniqueId().toString() : "UHConsole";

        if (args.length == 0) {
            sender.sendMessage(new ComponentBuilder("Usage: /mute <pseudo> [duration] [reason]").color(ChatColor.RED).create());
        } else {
            boolean duration = false;
            long muteUntilTime = -1;
            long muteTime = 0;
            if (args.length > 1) {
                Matcher m = timePattern.matcher(args[1]);
                while (m.find()) {
                    if (m.group() == null || m.group().isEmpty()) {
                        continue;
                    } else if (m.group(1) != null && !m.group(1).isEmpty() && m.group(2) != null && !m.group(2).isEmpty()) {
                        int number = Integer.parseInt(m.group(1));
                        String type = m.group(2);
                        duration = true;

                        switch (type) {
                            case "w":
                                muteTime += number * 604800000L;
                                break;
                            case "d":
                                muteTime += number * 86400000L;
                                break;
                            case "h":
                                muteTime += number * 3600000L;
                                break;
                            case "m":
                                muteTime += number * 60000L;
                                break;
                            case "s":
                                muteTime += number * 1000L;
                                break;
                        }
                    }
                }
            }
            if (muteTime > 604800000L) muteTime = 604800000L;
            if (!duration) muteTime = 604800000L;
            muteUntilTime = System.currentTimeMillis() + muteTime;

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
            muteUntilTime += (System.currentTimeMillis() - startTime);

            String muteName = args[0];

            UUID muteUUID = BungeeGuardUtils.getMB().getUuidFromName(muteName);
            String muteDurationStr = BungeeGuardUtils.getDuration(muteUntilTime);
            String muteMessage = MuteTypeVar.playerFormat(muteDurationStr, reason);

            BungeeGuardUtils.getMB().sendPlayerMessage(muteUUID, muteMessage);

            Mute alreadyMute = BungeeGuardUtils.getMute(muteUUID);
            if (alreadyMute != null)
                Main.mutes.remove(alreadyMute);

            Mute Mute = new Mute(muteUUID, muteName, muteUntilTime, reason, adminName, adminUUID);
            Mute.addToBdd();

            BungeeGuardUtils.getMB().sendChannelMessage("mute",
                    BungeeGuardUtils.getMB().getServerId() + MultiBungee.SEPARATOR + muteUUID + MultiBungee.SEPARATOR + muteName + MultiBungee.SEPARATOR + muteUntilTime + MultiBungee.SEPARATOR +
                            reason + MultiBungee.SEPARATOR + adminName + MultiBungee.SEPARATOR + adminUUID);

            String adminFormat = MuteTypeVar.adminFormat(muteDurationStr, reason, adminName, muteName);
            BungeeGuardUtils.getMB().notifyStaff(adminFormat);
        }
    }
}
