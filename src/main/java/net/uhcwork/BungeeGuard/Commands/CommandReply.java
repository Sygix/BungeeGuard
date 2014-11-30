package net.uhcwork.BungeeGuard.Commands;

import com.google.common.collect.ObjectArrays;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.uhcwork.BungeeGuard.Main;
import net.uhcwork.BungeeGuard.Models.BungeeMute;
import net.uhcwork.BungeeGuard.Utils.PrettyLinkComponent;

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

        if (!p.hasPermission("bungee.reply")) {
            return;
        }

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
        UUID destinataire = plugin.getReply(p.getUniqueId());
        if (destinataire == null) {
            p.sendMessage(new ComponentBuilder("Vous n'avez personne à qui répondre !").color(ChatColor.RED).create());
            return;
        }
        String destinataireName = Main.getMB().getNameFromUuid(destinataire);
        String message = "";
        for (String arg : args) message += arg + " ";

        if (!Main.getMB().isPlayerOnline(destinataire)) {
            p.sendMessage(new ComponentBuilder("Le joueur que vous chercher a contacter n'est pas en ligne !").color(ChatColor.RED).create());
            return;
        }
        if (plugin.getIgnoreManager().playerIgnores(destinataire, p.getUniqueId()) && !p.hasPermission("bungee.ignore.ignore")) {
            p.sendMessage(new ComponentBuilder("Ce joueur vous a ignoré.").color(ChatColor.RED).create());
            return;
        }
        if (plugin.getIgnoreManager().playerIgnores(p.getUniqueId(), destinataire) && !p.hasPermission("bungee.ignore.ignore")) {
            p.sendMessage(new ComponentBuilder("Vous ne pouvez pas parler a un joueur ignoré.").color(ChatColor.RED).create());
            return;
        }

        BaseComponent[] contenu = new ComponentBuilder("[").color(ChatColor.GRAY).append("Moi").color(ChatColor.GREEN).append(" ➠ ").color(ChatColor.GRAY).append(destinataireName).color(ChatColor.GREEN).append("]").color(ChatColor.GRAY).append(" ").create();

        if (p.hasPermission("bungee.colormsg"))
            contenu = ObjectArrays.concat(contenu, PrettyLinkComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', message)), BaseComponent.class);
        else
            contenu = ObjectArrays.concat(contenu, new TextComponent(message));

        p.sendMessage(contenu);
        plugin.setReply(p.getUniqueId(), destinataire);
        plugin.setReply(destinataire, p.getUniqueId());
        Main.getMB().sendPrivateMessage(p.getName(), destinataire, message);
    }
}
