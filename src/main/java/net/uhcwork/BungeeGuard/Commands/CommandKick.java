package net.uhcwork.BungeeGuard.Commands;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.uhcwork.BungeeGuard.Kick.KickType;
import net.uhcwork.BungeeGuard.Main;

public class CommandKick extends Command {

    public Main plugin;

    public CommandKick(Main plugin) {
        super("kick", "bungeeguard.kick");
        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        String adminName = (sender instanceof ProxiedPlayer) ? sender.getName() : "UHConsole";

        if (args.length == 0) {
            sender.sendMessage(new ComponentBuilder("Usage: /kick <pseudo> [reason]").color(ChatColor.RED).create());
        } else {
            String reason = "";
            if (args.length > 1) {
                for (int i = 1; i < args.length; i++) {
                    reason += " " + args[i];
                }
            }
            reason = reason.trim();
            if (plugin.isPremadeMessage(reason))
                reason = plugin.getPremadeMessage(reason);

            reason = ChatColor.translateAlternateColorCodes('&', reason);
            KickType KickTypeVar = (reason.isEmpty()) ? KickType.KICK : KickType.KICK_W_REASON;

            String bannedName = args[0];
            if (!Main.getMB().isPlayerOnline(bannedName)) {
                sender.sendMessage(new ComponentBuilder("Erreur: Ce joueur n'est pas en ligne.").color(ChatColor.RED).create());
                return;
            } else {
                Main.getMB().kickPlayer(bannedName, KickTypeVar.kickFormat(reason));
            }

            String adminFormat = KickTypeVar.adminFormat(reason, adminName, bannedName);
            Main.getMB().notifyStaff(adminFormat);
        }
    }
}
