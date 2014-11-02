package net.uhcwork.BungeeGuard.Commands;

import com.google.common.base.Joiner;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.uhcwork.BungeeGuard.Main;
import net.uhcwork.BungeeGuard.Managers.SanctionManager;
import net.uhcwork.BungeeGuard.Models.BungeeMute;

import java.util.Arrays;
import java.util.UUID;

public class CommandUnmute extends Command {

    private final Main plugin;
    private final SanctionManager MM;

    public CommandUnmute(Main plugin) {
        super("unmute", "bungee.mute");
        this.plugin = plugin;
        this.MM = plugin.getSanctionManager();
    }

    @Override
    public void execute(CommandSender sender, String[] args) {

        if (args.length == 0) {
            sender.sendMessage(new ComponentBuilder("Usage: /unmute <pseudo> [reason]").color(ChatColor.RED).create());
        } else if (args.length >= 1) {
            String unmuteName = (sender instanceof ProxiedPlayer) ? sender.getName() : "UHConsole";

            String reason = Joiner.on(" ").join(Arrays.copyOfRange(args, 1, args.length)).trim();

            String muteName = args[0];

            if (plugin.isPremadeMessage(reason))
                reason = plugin.getPremadeMessage(reason);
            reason = ChatColor.translateAlternateColorCodes('&', reason);

            UUID muteUUID = Main.getMB().getUuidFromName(muteName);

            BungeeMute mute = MM.findMute(muteUUID);
            if (mute == null) {
                sender.sendMessage(new ComponentBuilder("Erreur: Ce joueur n'est pas mute.").color(ChatColor.RED).create());
            } else {
                MM.unmute(mute, sender.getName(), reason, true);
                Main.getMB().unmutePlayer(muteUUID);

                String adminMessage = ChatColor.AQUA + unmuteName + ChatColor.RED + " a démute " + ChatColor.GREEN + muteName + ChatColor.RED;

                if (!reason.isEmpty())
                    adminMessage += " avec la raison:" + ChatColor.AQUA + reason + ChatColor.RED;

                Main.getMB().notifyStaff(adminMessage + ".");
            }
        }
    }

}
