package fr.PunKeel.BungeeGuard.Commands;

import fr.PunKeel.BungeeGuard.Main;
import fr.PunKeel.BungeeGuard.Models.BungeeMute;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import java.util.UUID;

import static net.md_5.bungee.api.chat.TextComponent.fromLegacyText;

public class CommandMsg extends Command {

    private final Main plugin;

    public CommandMsg(Main plugin) {
        super("msg", "bungee.msg", "m", "w", "tell", "whisper", "mp");
        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof ProxiedPlayer)) {
            sender.sendMessage(new ComponentBuilder("Vous devez être un joueur pour executer cette commande !").color(ChatColor.RED).create());
            return;
        }

        ProxiedPlayer p = (ProxiedPlayer) sender;

        BungeeMute mute = plugin.getSanctionManager().findMute(p.getUniqueId());
        if (mute != null) {
            p.sendMessage(fromLegacyText(mute.getMuteMessage()));
            return;
        }

        if (args.length == 0) {
            p.sendMessage(new ComponentBuilder("Exemple :").color(ChatColor.RED).create());
            p.sendMessage(new ComponentBuilder("/msg NomDeMonAmi Hey ça te dit de jouer avec moi ?").color(ChatColor.RED).create());
            return;
        }
        if (plugin.getIgnoreManager().playerIgnores(p.getUniqueId(), null) && !p.hasPermission("bungee.ignore.ignore")) {
            p.sendMessage(fromLegacyText(ChatColor.RED + "Vous avez desactivé les messages privés !"));
            return;
        }

        if (args.length >= 1) {
            if (!Main.getMB().isPlayerOnline(args[0])) {
                p.sendMessage(new ComponentBuilder("Le joueur que vous cherchez à contacter n'est pas en ligne !").color(ChatColor.RED).create());
                return;
            }
            if (args.length <= 1) {
                p.sendMessage(fromLegacyText(ChatColor.RED + "Votre message ne peut pas être vide !"));
                return;
            }
            UUID receiverUUID = Main.getMB().getUuidFromName(args[0]);
            if (!p.hasPermission("bungee.ignore.ignore")) {
                if (plugin.getIgnoreManager().playerIgnores(receiverUUID, null)) {
                    p.sendMessage(new ComponentBuilder("Ce joueur ne souhaite pas être contacté.").color(ChatColor.RED).create());
                    return;
                }

                if (plugin.getIgnoreManager().playerIgnores(receiverUUID, p.getUniqueId())) {
                    p.sendMessage(new ComponentBuilder("Ce joueur vous ignore.").color(ChatColor.RED).create());
                    return;
                }
                if (plugin.getIgnoreManager().playerIgnores(p.getUniqueId(), receiverUUID)) {
                    p.sendMessage(new ComponentBuilder("Vous ne pouvez pas parler à un joueur ignoré.").color(ChatColor.RED).create());
                    return;
                }
            }

            String text = "";
            for (int i = 1; i < args.length; i++)
                text += args[i] + " ";

            Main.getMB().sendPrivateMessage(p.getUniqueId(), receiverUUID, text);
        }
    }
}
