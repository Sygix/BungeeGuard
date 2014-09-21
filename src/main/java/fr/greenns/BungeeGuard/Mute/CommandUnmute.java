package fr.greenns.BungeeGuard.Mute;

import fr.greenns.BungeeGuard.BungeeGuardUtils;
import fr.greenns.BungeeGuard.Main;
import fr.greenns.BungeeGuard.Models.BungeeMute;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import java.util.UUID;

public class CommandUnmute extends Command {

    private MuteManager MM;
    public Main plugin;

    public CommandUnmute(Main plugin) {
        super("unmute", "bungeeguard.mute");
        this.plugin = plugin;
        this.MM = plugin.getMM();
    }

    @Override
    public void execute(CommandSender sender, String[] args) {

        if (args.length == 0) {
            sender.sendMessage(new ComponentBuilder("Usage: /unmute <pseudo> [reason]").color(ChatColor.RED).create());
        } else if (args.length >= 1) {
            String unmuteReason = "";
            String unmuteName = (sender instanceof ProxiedPlayer) ? sender.getName() : "UHConsole";

            if (args.length > 1) {
                for (int i = 1; i < args.length; i++)
                    unmuteReason += " " + args[i];
            }
            String muteName = args[0];

            UUID muteUUID = BungeeGuardUtils.getMB().getUuidFromName(muteName);

            BungeeMute mute = MM.findMute(muteUUID);
            if (mute == null) {
                sender.sendMessage(new ComponentBuilder("Erreur: Ce joueur n'est pas mute.").color(ChatColor.RED).create());
            } else {
                MM.unmute(mute, sender.getName(), unmuteReason, true);
                BungeeGuardUtils.getMB().unmutePlayer(muteUUID);

                MuteType muteType = (unmuteReason.equals("")) ? MuteType.UNMUTE : MuteType.UNMUTE_W_REASON;
                if (!unmuteReason.equals(""))
                    unmuteReason = ChatColor.translateAlternateColorCodes('&', unmuteReason);
                String adminFormat = muteType.adminFormat("", unmuteReason, unmuteName, muteName);
                BungeeGuardUtils.getMB().notifyStaff(adminFormat);
            }
        }
    }

}
