package net.uhcwork.BungeeGuard.Commands;

import com.google.common.base.Joiner;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.uhcwork.BungeeGuard.Main;
import net.uhcwork.BungeeGuard.Managers.SanctionManager;
import net.uhcwork.BungeeGuard.Models.BungeeBan;

import java.util.Arrays;
import java.util.UUID;

public class CommandUnban extends Command {

    private final Main plugin;
    private final SanctionManager BM;

    public CommandUnban(Main plugin) {
        super("unban", "bungee.ban");
        this.plugin = plugin;
        this.BM = plugin.getSanctionManager();
    }

    @Override
    public void execute(CommandSender sender, String[] args) {

        if (args.length == 0) {
            sender.sendMessage(new ComponentBuilder("Usage: /unban <pseudo> [reason]").color(ChatColor.RED).create());
            return;
        }
        String unbanName = (sender instanceof ProxiedPlayer) ? sender.getName() : "UHConsole";


        String reason = Joiner.on(" ").join(Arrays.copyOfRange(args, 1, args.length)).trim();

        if (plugin.isPremadeMessage(reason))
            reason = plugin.getPremadeMessage(reason);
        reason = ChatColor.translateAlternateColorCodes('&', reason);

        String bannedName = args[0];

        UUID bannedUUID = Main.getMB().getUuidFromName(bannedName);
        if (bannedUUID == null) {
            sender.sendMessage(new TextComponent(ChatColor.RED + "Erreur: joueur inconnu."));
            return;
        }

        BungeeBan ban = BM.findBan(bannedUUID);
        if (ban == null) {
            sender.sendMessage(new ComponentBuilder("Erreur: Ce joueur n'est pas banni.").color(ChatColor.RED).create());
        } else {
            BM.unban(ban, sender.getName(), reason, true);
            Main.getMB().unban(bannedUUID);


            String adminMessage = ChatColor.AQUA + unbanName + ChatColor.RED + " a débanni " + ChatColor.GREEN + bannedName + ChatColor.RED;
            if (!reason.isEmpty()) {
                adminMessage += " avec la raison:" + ChatColor.AQUA + reason + ChatColor.RED;
            }
            Main.getMB().notifyStaff(adminMessage + ".");
        }

    }

}
