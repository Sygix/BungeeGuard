package fr.greenns.BungeeGuard.commands;

import fr.greenns.BungeeGuard.BungeeGuardUtils;
import fr.greenns.BungeeGuard.Main;
import fr.greenns.BungeeGuard.Mute.Mute;
import fr.greenns.BungeeGuard.Mute.MuteType;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ComponentBuilder;
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

        Mute MuteUser = BungeeGuardUtils.getMute(p.getUniqueId());
        if (MuteUser != null) {
            if (MuteUser.isMute()) {
                MuteType MuteType = (MuteUser.getReason() != null) ? fr.greenns.BungeeGuard.Mute.MuteType.NON_PERMANENT_W_REASON : fr.greenns.BungeeGuard.Mute.MuteType.NON_PERMANENT;
                String MuteMsg = MuteType.playerFormat("", MuteUser.getReason());
                p.sendMessage(new ComponentBuilder(MuteMsg).create());
            } else {
                MuteUser.removeFromBDD("TimeEnd", "Automatique");
            }
            return;
        }

        if (args.length == 0) {
            p.sendMessage(new ComponentBuilder("La bonne commande est :").color(ChatColor.RED).create());
            p.sendMessage(new ComponentBuilder("/r je te répond après").color(ChatColor.RED).create());
            return;
        }
        String destinataire = plugin.reply.get(p.getUniqueId());
        if (destinataire == null) {
            p.sendMessage(new ComponentBuilder("Vous n'avez personne à qui répondre !").color(ChatColor.RED).create());
            return;
        }
        UUID receiverUUID = BungeeGuardUtils.getMB().getUuidFromName(destinataire);
        String message = "";
        for (String arg : args) message += arg + " ";

        if (!BungeeGuardUtils.getMB().isPlayerOnline(receiverUUID)) {
            p.sendMessage(new ComponentBuilder("Le joueur que vous chercher a contacter n'est pas en ligne !").color(ChatColor.RED).create());
            return;
        }
        if ((plugin.ignore.containsKey(receiverUUID) && plugin.ignore.get(receiverUUID).size() != 0 && plugin.ignore.get(receiverUUID).contains(p.getUniqueId())) && !p.hasPermission("bungeeguard.ignore.ignore")) {
            p.sendMessage(new ComponentBuilder("Ce joueur vous a ignoré.").color(ChatColor.RED).create());
            return;
        }
        if ((plugin.ignore.containsKey(p.getUniqueId()) && plugin.ignore.get(p.getUniqueId()).size() != 0 && plugin.ignore.get(p.getUniqueId()).contains(receiverUUID)) && !p.hasPermission("bungeeguard.ignore.ignore")) {
            p.sendMessage(new ComponentBuilder("Vous ne pouvez pas parler a un joueur ignoré.").color(ChatColor.RED).create());
            return;
        }

        if (p.hasPermission("bungeeguard.colormsg"))
            message = ChatColor.translateAlternateColorCodes('&', message);

        p.sendMessage(new ComponentBuilder("[").color(ChatColor.GRAY).append("Moi").color(ChatColor.GREEN).append(" ➠ ").color(ChatColor.GRAY).append(destinataire).color(ChatColor.GREEN).append("]").color(ChatColor.GRAY).append(" " + message).create());
        plugin.reply.put(p.getUniqueId(), destinataire);
        BungeeGuardUtils.getMB().sendPrivateMessage(p.getName(), receiverUUID, message);
    }
}
