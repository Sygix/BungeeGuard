package fr.greenns.BungeeGuard.commands;

import com.google.common.collect.ObjectArrays;
import fr.greenns.BungeeGuard.BungeeGuardUtils;
import fr.greenns.BungeeGuard.Main;
import fr.greenns.BungeeGuard.Models.BungeeMute;
import fr.greenns.BungeeGuard.Mute.MuteType;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import java.util.UUID;

public class CommandReply extends Command {

    public Main plugin;

    public CommandReply(Main plugin) {
        super("r", "bungeeguard.reply");
        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof ProxiedPlayer)) {
            sender.sendMessage(new ComponentBuilder("Vous devez être un joueur pour exécuter cette commande !").color(ChatColor.RED).create());
            return;
        }
        ProxiedPlayer p = (ProxiedPlayer) sender;

        if (!p.hasPermission("bungeeguard.reply")) {
            return;
        }

        BungeeMute mute = plugin.getMM().findMute(p.getUniqueId());
        if (mute != null) {
            if (mute.isMute()) {
                MuteType muteType = (mute.getReason() != null) ? fr.greenns.BungeeGuard.Mute.MuteType.NON_PERMANENT_W_REASON : fr.greenns.BungeeGuard.Mute.MuteType.NON_PERMANENT;
                String MuteMsg = muteType.playerFormat("", mute.getReason());
                p.sendMessage(new ComponentBuilder(MuteMsg).create());
            } else {
                plugin.getMM().unmute(mute, "TimeEnd", "Automatique", true);
                BungeeGuardUtils.getMB().unmutePlayer(p.getUniqueId());
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
        String destinataireName = BungeeGuardUtils.getMB().getNameFromUuid(destinataire);
        String message = "";
        for (String arg : args) message += arg + " ";

        if (!BungeeGuardUtils.getMB().isPlayerOnline(destinataire)) {
            p.sendMessage(new ComponentBuilder("Le joueur que vous chercher a contacter n'est pas en ligne !").color(ChatColor.RED).create());
            return;
        }
        if (plugin.getIM().playerIgnores(destinataire, p.getUniqueId()) && !p.hasPermission("bungeeguard.ignore.ignore")) {
            p.sendMessage(new ComponentBuilder("Ce joueur vous a ignoré.").color(ChatColor.RED).create());
            return;
        }
        if (plugin.getIM().playerIgnores(p.getUniqueId(), destinataire) && !p.hasPermission("bungeeguard.ignore.ignore")) {
            p.sendMessage(new ComponentBuilder("Vous ne pouvez pas parler a un joueur ignoré.").color(ChatColor.RED).create());
            return;
        }

        BaseComponent[] contenu = new ComponentBuilder("[").color(ChatColor.GRAY).append("Moi").color(ChatColor.GREEN).append(" ➠ ").color(ChatColor.GRAY).append(destinataireName).color(ChatColor.GREEN).append("]").color(ChatColor.GRAY).append(" ").create();

        if (p.hasPermission("bungeeguard.colormsg"))
            contenu = ObjectArrays.concat(contenu, TextComponent.fromLegacyText(message), BaseComponent.class);
        else
            contenu = ObjectArrays.concat(contenu, new TextComponent(message));

        p.sendMessage(contenu);
        plugin.setReply(p.getUniqueId(), destinataire);
        BungeeGuardUtils.getMB().sendPrivateMessage(p.getName(), destinataire, message);
    }
}
