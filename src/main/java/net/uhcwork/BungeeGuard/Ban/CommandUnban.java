package net.uhcwork.BungeeGuard.Ban;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.uhcwork.BungeeGuard.BungeeGuardUtils;
import net.uhcwork.BungeeGuard.Main;
import net.uhcwork.BungeeGuard.Models.BungeeBan;

import java.util.UUID;

public class CommandUnban extends Command {

    public Main plugin;
    BanManager BM;

    public CommandUnban(Main plugin) {
        super("unban", "bungeeguard.ban");
        this.plugin = plugin;
        this.BM = plugin.getBM();
    }

    @Override
    public void execute(CommandSender sender, String[] args) {

        if (args.length == 0) {
            sender.sendMessage(new ComponentBuilder("Usage: /unban <pseudo> [reason]").color(ChatColor.RED).create());
        }
        String unbanReason = "";
        String unbanName = (sender instanceof ProxiedPlayer) ? sender.getName() : "UHConsole";

        if (args.length > 1) {
            for (int i = 1; i < args.length; i++)
                unbanReason += " " + args[i];
        }
        unbanReason = unbanReason.trim();

        if (plugin.isPremadeMessage(unbanReason))
            unbanReason = plugin.getPremadeMessage(unbanReason);
        String bannedName = args[0];

        UUID bannedUUID = BungeeGuardUtils.getMB().getUuidFromName(bannedName);
        if (bannedUUID == null) {
            sender.sendMessage(new TextComponent(ChatColor.RED + "Erreur: joueur inconnu."));
            return;
        }

        BungeeBan ban = BM.findBan(bannedUUID);
        if (ban == null) {
            sender.sendMessage(new ComponentBuilder("Erreur: Ce joueur n'est pas banni.").color(ChatColor.RED).create());
        } else {
            BM.unban(ban, sender.getName(), unbanReason, true);
            BungeeGuardUtils.getMB().unban(bannedUUID);
            BanType banType = (unbanReason.equals("")) ? BanType.UNBAN : BanType.UNBAN_W_REASON;
            if (!unbanReason.isEmpty())
                unbanReason = ChatColor.translateAlternateColorCodes('&', unbanReason);

            String adminFormat = banType.adminFormat("", unbanReason, unbanName, bannedName);
            BungeeGuardUtils.getMB().notifyStaff(adminFormat);
        }

    }

}
