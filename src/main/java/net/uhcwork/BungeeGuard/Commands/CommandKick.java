package net.uhcwork.BungeeGuard.Commands;

import com.google.common.base.Joiner;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.uhcwork.BungeeGuard.Main;

import java.util.Arrays;

public class CommandKick extends Command {

    private final Main plugin;

    public CommandKick(Main plugin) {
        super("kick", "bungee.kick");
        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        String adminName = (sender instanceof ProxiedPlayer) ? sender.getName() : "UHConsole";

        if (args.length == 0) {
            sender.sendMessage(new ComponentBuilder("Usage: /kick <pseudo> [reason]").color(ChatColor.RED).create());
        } else {
            String reason = Joiner.on(" ").join(Arrays.copyOfRange(args, 1, args.length)).trim();
            if (plugin.isPremadeMessage(reason))
                reason = plugin.getPremadeMessage(reason);

            reason = ChatColor.translateAlternateColorCodes('&', reason);

            String kickedName = args[0];

            String kickMessage = ChatColor.RED + "Vous avez été kické du serveur";
            String adminNotification = ChatColor.AQUA + adminName + ChatColor.RED + " a kick " + ChatColor.GREEN + kickedName + ChatColor.RED;
            if (!reason.isEmpty()) {
                kickMessage += " pour:\n" + ChatColor.AQUA + reason + ChatColor.RED;
                adminNotification += " pour:\n" + ChatColor.AQUA + reason + ChatColor.RED;
            }
            kickMessage += ".";
            adminNotification += ".";

            Main.getMB().kickPlayer(kickedName, kickMessage);

            sender.sendMessage(new ComponentBuilder("Joueur expulsé.").color(ChatColor.RED).create());

            Main.getMB().notifyStaff(adminNotification);
        }
    }
}
