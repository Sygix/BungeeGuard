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
import net.uhcwork.BungeeGuard.MultiBungee.MultiBungee;

import java.util.Arrays;
import java.util.UUID;

public class CommandUnban extends Command {
    private final SanctionManager SM;
    private final MultiBungee MB;

    public CommandUnban(Main plugin) {
        super("unban", "bungee.ban");
        this.SM = plugin.getSanctionManager();
        this.MB = Main.getMB();
    }

    @Override
    public void execute(CommandSender sender, String[] args) {

        if (args.length == 0) {
            sender.sendMessage(new ComponentBuilder("Usage: /unban <pseudo> [reason]").color(ChatColor.RED).create());
            return;
        }
        String unbanName = (sender instanceof ProxiedPlayer) ? sender.getName() : "UHConsole";


        String reason = Joiner.on(" ").join(Arrays.copyOfRange(args, 1, args.length)).trim();

        if (SM.isPremadeMessage(reason))
            reason = SM.getPremadeMessage(reason);
        reason = ChatColor.translateAlternateColorCodes('&', reason);

        String bannedName = args[0];

        UUID bannedUUID = MB.getUuidFromName(bannedName);
        if (bannedUUID == null) {
            sender.sendMessage(new TextComponent(ChatColor.RED + "Erreur: joueur inconnu."));
            return;
        }

        BungeeBan ban = SM.findBan(bannedUUID);
        if (ban == null) {
            sender.sendMessage(new ComponentBuilder("Erreur: Ce joueur n'est pas banni.").color(ChatColor.RED).create());
        } else {
            SM.unban(ban, sender.getName(), reason, true);
            MB.unban(bannedUUID);


            String adminMessage = ChatColor.AQUA + unbanName + ChatColor.RED + " a débanni " + ChatColor.GREEN + bannedName + ChatColor.RED;
            if (!reason.isEmpty()) {
                adminMessage += " avec la raison:" + ChatColor.AQUA + reason + ChatColor.RED;
            }
            MB.notifyStaff(adminMessage + ".");
        }

    }

}
