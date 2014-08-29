package fr.greenns.BungeeGuard.commands;

import fr.greenns.BungeeGuard.BungeeGuard;
import fr.greenns.BungeeGuard.BungeeGuardUtils;
import fr.greenns.BungeeGuard.Mute.Mute;
import fr.greenns.BungeeGuard.Mute.MuteType;
import fr.greenns.BungeeGuard.utils.ComponentManager;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import org.bukkit.entity.Player;

import java.util.UUID;

public class CommandMsg extends Command {

    public BungeeGuard plugin;

    public CommandMsg(BungeeGuard plugin) {
        super("msg", "bungeeguard.msg", "m", "w", "tell", "whisper");
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
            p.sendMessage(new ComponentBuilder("/msg NomDeMonAmi Hey sa te dit de jouer avec moi ?").color(ChatColor.RED).create());
            return;
        }

        if (args.length >= 1) {
            if (args[0].equalsIgnoreCase(p.getName())) {
                p.sendMessage(new ComponentBuilder("Vous ne pouvez pas envoyer un message à vous-même !").color(ChatColor.RED).create());
            }
            if (BungeeGuardUtils.getMB().isPlayerOnline(args[0])) {
                if (args.length > 1) {
                    UUID receiverUUID = BungeeGuardUtils.getMB().getUuidFromName(args[0]);
                    ProxiedPlayer pl = BungeeCord.getInstance().getPlayer(receiverUUID);
                    if(pl.c)
                    if (!pl.hasPermission("bungeeguard.moremsg") || p.hasPermission("bungeeguard.moremsg") || (plugin.reply.containsKey(p.getName()) && plugin.reply.get(p.getName()).equals(pl))) {
                        if (!(plugin.ignore.containsKey(pl.getUniqueId()) && plugin.ignore.get(pl.getUniqueId()).size() != 0 && plugin.ignore.get(pl.getUniqueId()).contains(p.getUniqueId())) || pl.hasPermission("bungeeguard.ignore.ignore")) {
                            if (!(plugin.ignore.containsKey(p.getUniqueId()) && plugin.ignore.get(p.getUniqueId()).size() != 0 && plugin.ignore.get(p.getUniqueId()).contains(pl.getUniqueId())) || p.hasPermission("bungeeguard.ignore.ignore")) {
                                String text1 = "";
                                for (int i = 1; i < args.length; i++)
                                    text1 = text1 + args[i] + " ";

                                String text = text1;
                                if (p.hasPermission("bungeeguard.colormsg"))
                                    text = ChatColor.translateAlternateColorCodes('&', text);
                                BungeeGuardUtils.getMB().sendPrivateMessage(p.getName(), pl.getName(), text)
                                p.sendMessage(new ComponentBuilder("[").color(ChatColor.GRAY).append("Moi").color(ChatColor.GREEN).append(" ➠ ").color(ChatColor.GRAY).append(pl.getName()).color(ChatColor.GREEN).append("]").color(ChatColor.GRAY).append(" " + text).create());


                                plugin.reply.put(p.getName(), pl);
                                plugin.reply.put(pl.getName(), p);
                            } else {
                                p.sendMessage(new ComponentBuilder("Vous ne pouvez pas parler a un joueur ignoré.").color(ChatColor.RED).create());
                            }
                        } else {
                            p.sendMessage(new ComponentBuilder("Ce joueur vous a ignoré.").color(ChatColor.RED).create());
                        }
                    } else {
                        p.sendMessage(new ComponentBuilder("Vous n'avez pas la permission de parler à ce joueur !").color(ChatColor.RED).create());
                    }
                } else {
                    p.sendMessage(ComponentManager.generate(ChatColor.RED + "Votre message ne peut pas être vide !"));
                }
            } else {
                p.sendMessage(new ComponentBuilder("Le joueur que vous chercher a contacter n'est pas en ligne !").color(ChatColor.RED).create());
            }

        }
    }
}
