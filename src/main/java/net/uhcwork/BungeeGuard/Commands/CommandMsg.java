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

public class CommandMsg extends Command {

    private final Main plugin;

    public CommandMsg(Main plugin) {
        super("msg", "bungee.msg", "m", "w", "tell", "whisper", "mp");
        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof ProxiedPlayer)) {
            sender.sendMessage(new ComponentBuilder("Vous devez etre un joueur pour executer cette commande !").color(ChatColor.RED).create());
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
            p.sendMessage(new ComponentBuilder("Exemple :").color(ChatColor.RED).create());
            p.sendMessage(new ComponentBuilder("/msg NomDeMonAmi Hey ça te dit de jouer avec moi ?").color(ChatColor.RED).create());
            return;
        }

        if (args.length >= 1) {
            if (!Main.getMB().isPlayerOnline(args[0])) {
                p.sendMessage(new ComponentBuilder("Le joueur que vous chercher a contacter n'est pas en ligne !").color(ChatColor.RED).create());
                return;
            }
            if (args.length <= 1) {
                p.sendMessage(new TextComponent(ChatColor.RED + "Votre message ne peut pas être vide !"));
                return;
            }
            UUID receiverUUID = Main.getMB().getUuidFromName(args[0]);
            if (!p.hasPermission("bungee.ignore.ignore")) {
                if (plugin.getIgnoreManager().playerIgnores(receiverUUID, null)) {
                    p.sendMessage(new ComponentBuilder("Ce joueur ne souhaite pas être contacté.").color(ChatColor.RED).create());
                    return;
                }

                if (plugin.getIgnoreManager().playerIgnores(receiverUUID, p.getUniqueId())) {
                    p.sendMessage(new ComponentBuilder("Ce joueur vous a ignoré.").color(ChatColor.RED).create());
                    return;
                }
                if (plugin.getIgnoreManager().playerIgnores(p.getUniqueId(), receiverUUID)) {
                    p.sendMessage(new ComponentBuilder("Vous ne pouvez pas parler a un joueur ignoré.").color(ChatColor.RED).create());
                    return;
                }
            }

            String text = "";
            for (int i = 1; i < args.length; i++)
                text += args[i] + " ";

            Main.getMB().sendPrivateMessage(p.getName(), receiverUUID, text);

            BaseComponent[] contenu = new ComponentBuilder("[").color(ChatColor.GRAY)
                    .append("Moi").color(ChatColor.GREEN)
                    .append(" ➠ ").color(ChatColor.GRAY)
                    .append(args[0]).color(ChatColor.GREEN)
                    .append("]").color(ChatColor.GRAY)
                    .append(" ")
                    .create();

            if (p.hasPermission("bungee.colormsg"))
                contenu = ObjectArrays.concat(contenu, PrettyLinkComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', text)), BaseComponent.class);
            else
                contenu = ObjectArrays.concat(contenu, new TextComponent(text));

            p.sendMessage(contenu);


            plugin.setReply(receiverUUID, p.getUniqueId());
            plugin.setReply(p.getUniqueId(), receiverUUID);
        }
    }
}
