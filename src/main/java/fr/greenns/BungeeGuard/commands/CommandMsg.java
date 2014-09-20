package fr.greenns.BungeeGuard.commands;

import com.google.common.collect.ObjectArrays;
import fr.greenns.BungeeGuard.BungeeGuardUtils;
import fr.greenns.BungeeGuard.Main;
import fr.greenns.BungeeGuard.Mute.Mute;
import fr.greenns.BungeeGuard.Mute.MuteType;
import fr.greenns.BungeeGuard.utils.Permissions;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import java.util.UUID;

public class CommandMsg extends Command {

    public Main plugin;

    public CommandMsg(Main plugin) {
        super("msg", "bungeeguard.msg", "m", "w", "tell", "whisper", "mp");
        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (sender instanceof ProxiedPlayer) {
            ProxiedPlayer p = (ProxiedPlayer) sender;

            if (!p.hasPermission("bungeeguard.msg")) {
                return;
            }
        } else {
            sender.sendMessage(new ComponentBuilder("Vous devez etre un joueur pour executer cette command !").color(ChatColor.RED).create());
            return;
        }

        ProxiedPlayer p = (ProxiedPlayer) sender;

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
            p.sendMessage(new ComponentBuilder("Exemple :").color(ChatColor.RED).create());
            p.sendMessage(new ComponentBuilder("/msg NomDeMonAmi Hey ça te dit de jouer avec moi ?").color(ChatColor.RED).create());
            return;
        }

        if (args.length >= 1) {
            if (args[0].equalsIgnoreCase(p.getName())) {
                p.sendMessage(new ComponentBuilder("Vous ne pouvez pas envoyer un message à vous-même !").color(ChatColor.RED).create());
                return;
            }
            if (!BungeeGuardUtils.getMB().isPlayerOnline(args[0])) {
                p.sendMessage(new ComponentBuilder("Le joueur que vous chercher a contacter n'est pas en ligne !").color(ChatColor.RED).create());
                return;
            }
            if (args.length <= 1) {
                p.sendMessage(new TextComponent(ChatColor.RED + "Votre message ne peut pas être vide !"));
                return;
            }
            UUID receiverUUID = BungeeGuardUtils.getMB().getUuidFromName(args[0]);
            boolean isReply = plugin.reply.containsKey(p.getUniqueId()) || plugin.reply.get(p.getUniqueId()).equals(receiverUUID.toString());
            if (Permissions.hasPerm(args[0], "bungeeguard.moremsg") && !p.hasPermission("bungeeguard.moremsg") && !isReply) {
                p.sendMessage(new ComponentBuilder("Vous n'avez pas la permission de parler à ce joueur !").color(ChatColor.RED).create());
                return;
            }
            if ((plugin.ignore.containsKey(receiverUUID) && plugin.ignore.get(receiverUUID).size() != 0 && plugin.ignore.get(receiverUUID).contains(p.getUniqueId())) && !Permissions.hasPerm(args[0], "bungeeguard.ignore.ignore")) {
                p.sendMessage(new ComponentBuilder("Ce joueur vous a ignoré.").color(ChatColor.RED).create());
                return;
            }
            if ((plugin.ignore.containsKey(p.getUniqueId()) && plugin.ignore.get(p.getUniqueId()).size() != 0 && plugin.ignore.get(p.getUniqueId()).contains(receiverUUID)) && !p.hasPermission("bungeeguard.ignore.ignore")) {
                p.sendMessage(new ComponentBuilder("Vous ne pouvez pas parler a un joueur ignoré.").color(ChatColor.RED).create());
                return;
            }
            String text = "";
            for (int i = 1; i < args.length; i++)
                text += args[i] + " ";

            BungeeGuardUtils.getMB().sendPrivateMessage(p.getName(), receiverUUID, text);

            BaseComponent[] contenu = new ComponentBuilder("[").color(ChatColor.GRAY).append("Moi").color(ChatColor.GREEN).append(" ➠ ").color(ChatColor.GRAY).append(args[0]).color(ChatColor.GREEN).append("]").color(ChatColor.GRAY).append(" ").create();

            if (p.hasPermission("bungeeguard.colormsg"))
                contenu = ObjectArrays.concat(contenu, TextComponent.fromLegacyText(text), BaseComponent.class);
            else
                contenu = ObjectArrays.concat(contenu, new TextComponent(text));

            p.sendMessage(contenu);


            plugin.reply.put(p.getUniqueId(), args[0]);
        }
    }
}
