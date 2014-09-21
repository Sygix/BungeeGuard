package fr.greenns.BungeeGuard.commands;

import fr.greenns.BungeeGuard.BungeeGuardUtils;
import fr.greenns.BungeeGuard.Main;
import fr.greenns.BungeeGuard.MultiBungee.MultiBungee;
import fr.greenns.BungeeGuard.utils.Permissions;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import java.util.UUID;

/**
 * Part of ${PACKAGE_NAME} (${PROJECT_NAME})
 * Date: 07/07/14
 * Time: 18:45
 * May be open-source & be sold (by Greenns, of course !)
 */
public class CommandIgnore extends Command {

    public Main plugin;

    public CommandIgnore(Main plugin) {
        super("ignore", "bungeeguard.ignore");
        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof ProxiedPlayer)) {
            sender.sendMessage(new ComponentBuilder("Vous devez etre un joueur pour executer cette commande !").color(ChatColor.RED).create());
            return;
        }
        ProxiedPlayer p = (ProxiedPlayer) sender;
        MultiBungee MB = BungeeGuardUtils.getMB();
        if (args.length != 1) {
            p.sendMessage(TextComponent.fromLegacyText(ChatColor.RED + "Utilisation: /ignore <joueur>"));
            return;
        }
        UUID toIgnore = MB.getUuidFromName(args[0]);
        if (!MB.isPlayerOnline(toIgnore)) {
            p.sendMessage(TextComponent.fromLegacyText(ChatColor.RED + "Ce joueur n'est pas en ligne."));
            return;
        }
        if (Permissions.hasPerm(args[0], "bungeeguard.ignore.ignore")) {
            p.sendMessage(TextComponent.fromLegacyText(ChatColor.RED + "Vous n'avez pas la permission d'ignorer ce joueur."));
            return;
        }
        if (plugin.getIM().playerIgnores(p.getUniqueId(), toIgnore)) {
            p.sendMessage(TextComponent.fromLegacyText(ChatColor.GRAY + "Vous n'ignorez plus " + ChatColor.AQUA + args[0] + ChatColor.GRAY + "."));
            BungeeGuardUtils.getMB().ignorePlayer(p.getUniqueId(), '-', toIgnore);
        } else {
            p.sendMessage(TextComponent.fromLegacyText(ChatColor.GRAY + "Vous ignorez maintenant " + ChatColor.AQUA + args[0] + ChatColor.GRAY + "."));
            BungeeGuardUtils.getMB().ignorePlayer(p.getUniqueId(), '+', toIgnore);
        }
    }
}