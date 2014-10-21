package net.uhcwork.BungeeGuard.Commands;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.uhcwork.BungeeGuard.BungeeGuardUtils;
import net.uhcwork.BungeeGuard.Main;

public class CommandSilence extends Command {

    public Main plugin;

    public CommandSilence(Main plugin) {
        super("silence", "bungee.silence");
        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof ProxiedPlayer)) {
            sender.sendMessage(new ComponentBuilder("Cette commande fonctionne uniquement en mode joueur !").color(ChatColor.RED).create());
            return;
        }

        ProxiedPlayer p = (ProxiedPlayer) sender;

        if (args.length == 0) {
            String servName = p.getServer().getInfo().getName();
            boolean silenced = !plugin.isSilenced(servName);
            Main.getMB().silenceServer(servName, silenced);
            Main.getMB().notifyStaff(BungeeGuardUtils.getStaffBroadcastTag() + ChatColor.GRAY + "Le chat du serveur "
                    + ChatColor.AQUA + servName + ChatColor.GRAY +
                    " a été " +
                    (silenced ? ChatColor.RED + "désactivé " : ChatColor.GREEN + "activé")
                    + ChatColor.GRAY + "!");
        } else {
            BungeeGuardUtils.msgPluginCommand(sender);
        }
    }

}
