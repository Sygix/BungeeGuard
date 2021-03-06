package fr.PunKeel.BungeeGuard.Commands;

import fr.PunKeel.BungeeGuard.Main;
import fr.PunKeel.BungeeGuard.MultiBungee.MultiBungee;
import fr.PunKeel.BungeeGuard.Permissions.Permissions;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import java.util.UUID;

public class CommandIgnore extends Command {

    private final Main plugin;

    public CommandIgnore(Main plugin) {
        super("ignore", "bungee.ignore");
        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof ProxiedPlayer)) {
            sender.sendMessage(TextComponent.fromLegacyText(String.valueOf(plugin.getIgnoreManager().getIgnoreList())));
            sender.sendMessage(new ComponentBuilder("Vous devez etre un joueur pour executer cette commande !").color(ChatColor.RED).create());
            return;
        }
        ProxiedPlayer p = (ProxiedPlayer) sender;
        MultiBungee MB = Main.getMB();
        if (args.length != 1) {
            p.sendMessage(TextComponent.fromLegacyText(ChatColor.RED + "Utilisation: /ignore <joueur>" + ChatColor.GRAY + "| " + ChatColor.RED + "Empêche le joueur de vous contacter"));
            p.sendMessage(TextComponent.fromLegacyText(ChatColor.RED + "/ignore * " + ChatColor.GRAY + "| " + ChatColor.RED + "Bloque tous les messages privés"));
            return;
        }
        UUID toIgnore = null;
        if (!args[0].equals("*")) {
            toIgnore = MB.getUuidFromName(args[0]);
            if (!MB.isPlayerOnline(toIgnore)) {
                p.sendMessage(TextComponent.fromLegacyText(ChatColor.RED + "Ce joueur n'est pas en ligne."));
                return;
            }
            if (Permissions.hasPerm(toIgnore, "bungeeguard.ignore.ignore")) {
                p.sendMessage(TextComponent.fromLegacyText(ChatColor.RED + "Vous n'avez pas la permission d'ignorer ce joueur."));
                return;
            }
        }

        if (plugin.getIgnoreManager().playerIgnores(p.getUniqueId(), toIgnore)) {
            if (toIgnore != null)
                p.sendMessage(TextComponent.fromLegacyText(ChatColor.GRAY + "Vous n'ignorez plus " + ChatColor.AQUA + args[0] + ChatColor.GRAY + "."));
            else
                p.sendMessage(TextComponent.fromLegacyText(ChatColor.GRAY + "Vous venez de débloquer les " + ChatColor.GREEN + "messages privés" + ChatColor.GRAY + "."));
            MB.ignorePlayer(p.getUniqueId(), '-', toIgnore);
        } else {
            if (toIgnore != null)
                p.sendMessage(TextComponent.fromLegacyText(ChatColor.GRAY + "Vous n'ignorez plus " + ChatColor.AQUA + args[0] + ChatColor.GRAY + "."));
            else
                p.sendMessage(TextComponent.fromLegacyText(ChatColor.GRAY + "Vous venez de bloquer les " + ChatColor.GREEN + "messages privés" + ChatColor.GRAY + "."));
            MB.ignorePlayer(p.getUniqueId(), '+', toIgnore);
        }
    }
}