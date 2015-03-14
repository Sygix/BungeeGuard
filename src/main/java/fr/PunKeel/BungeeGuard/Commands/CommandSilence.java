package fr.PunKeel.BungeeGuard.Commands;

import fr.PunKeel.BungeeGuard.BungeeGuardUtils;
import fr.PunKeel.BungeeGuard.Main;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class CommandSilence extends Command {

    private final Main plugin;

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
            Main.getMB().notifyStaff(Main.ADMIN_TAG + ChatColor.GRAY + "Le chat du serveur "
                    + ChatColor.AQUA + servName + ChatColor.GRAY +
                    " a été " +
                    (silenced ? ChatColor.RED + "désactivé " : ChatColor.GREEN + "activé")
                    + ChatColor.GRAY + "!");
        } else {
            BungeeGuardUtils.msgPluginCommand(sender);
        }
    }

}
