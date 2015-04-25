package fr.PunKeel.BungeeGuard.Commands;

import fr.PunKeel.BungeeGuard.Main;
import fr.PunKeel.BungeeGuard.Models.BungeeMute;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import java.util.UUID;

public class CommandReply extends Command {

    private final Main plugin;

    public CommandReply(Main plugin) {
        super("r", "bungee.reply", "reply");
        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof ProxiedPlayer)) {
            sender.sendMessage(new ComponentBuilder("Vous devez être un joueur pour exécuter cette commande !").color(ChatColor.RED).create());
            return;
        }

        ProxiedPlayer p = (ProxiedPlayer) sender;

        BungeeMute mute = plugin.getSanctionManager().findMute(p.getUniqueId());
        if (mute != null) {
            if (mute.isMute()) {
                p.sendMessage(TextComponent.fromLegacyText(mute.getMuteMessage()));
            } else {
                plugin.getSanctionManager().unmute(mute, "TimeEnd", "Automatique", true);
                Main.getMB().unmutePlayer(p.getUniqueId());
            }
            return;
        }

        if (args.length == 0) {
            p.sendMessage(new ComponentBuilder("La bonne commande est :").color(ChatColor.RED).create());
            p.sendMessage(new ComponentBuilder("/r je te répond après").color(ChatColor.RED).create());
            return;
        }

        if (plugin.getIgnoreManager().playerIgnores(p.getUniqueId(), null) && !p.hasPermission("bungee.ignore.ignore")) {
            p.sendMessage(TextComponent.fromLegacyText(ChatColor.RED + "Vous avez desactivé les messages privés !"));
            return;
        }

        UUID destinataire = plugin.getReply(p.getUniqueId());
        if (destinataire == null) {
            p.sendMessage(new ComponentBuilder("Vous n'avez personne à qui répondre !").color(ChatColor.RED).create());
            return;
        }
        String message = "";
        for (String arg : args)
            message += arg + " ";

        if (!Main.getMB().isPlayerOnline(destinataire)) {
            p.sendMessage(new ComponentBuilder("Le joueur que vous cherchez à contacter n'est pas en ligne !").color(ChatColor.RED).create());
            return;
        }

        if (plugin.getIgnoreManager().playerIgnores(destinataire, null) && !p.hasPermission("bungee.ignore.ignore")) {
            p.sendMessage(new ComponentBuilder("Ce joueur ne souhaite pas être contacté.").color(ChatColor.RED).create());
            return;
        }

        if (plugin.getIgnoreManager().playerIgnores(destinataire, p.getUniqueId()) && !p.hasPermission("bungee.ignore.ignore")) {
            p.sendMessage(new ComponentBuilder("Ce joueur vous ignore.").color(ChatColor.RED).create());
            return;
        }
        if (plugin.getIgnoreManager().playerIgnores(p.getUniqueId(), destinataire) && !p.hasPermission("bungee.ignore.ignore")) {
            p.sendMessage(new ComponentBuilder("Vous ne pouvez pas parler a un joueur ignoré.").color(ChatColor.RED).create());
            return;
        }

        Main.getMB().sendPrivateMessage(p.getUniqueId(), destinataire, message);
    }
}
