package net.uhcwork.BungeeGuard.Commands;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.uhcwork.BungeeGuard.Main;
import net.uhcwork.BungeeGuard.Managers.MuteManager;
import net.uhcwork.BungeeGuard.Models.BungeeMute;

import java.util.UUID;

public class CommandUnmute extends Command {

    public Main plugin;
    private MuteManager MM;

    public CommandUnmute(Main plugin) {
        super("unmute", "bungee.mute");
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

            unmuteReason = unmuteReason.trim();

            if (plugin.isPremadeMessage(unmuteReason))
                unmuteReason = plugin.getPremadeMessage(unmuteReason);
            UUID muteUUID = Main.getMB().getUuidFromName(muteName);

            BungeeMute mute = MM.findMute(muteUUID);
            if (mute == null) {
                sender.sendMessage(new ComponentBuilder("Erreur: Ce joueur n'est pas mute.").color(ChatColor.RED).create());
            } else {
                MM.unmute(mute, sender.getName(), unmuteReason, true);
                Main.getMB().unmutePlayer(muteUUID);

                String adminMessage = ChatColor.AQUA + unmuteName + ChatColor.RED + " a démute " + ChatColor.GREEN + muteName + ChatColor.RED;

                if (!unmuteReason.isEmpty())
                    adminMessage += " avec la raison:" + ChatColor.AQUA + ChatColor.translateAlternateColorCodes('&', unmuteReason) + ChatColor.RED;

                Main.getMB().notifyStaff(adminMessage + ".");
            }
        }
    }

}
