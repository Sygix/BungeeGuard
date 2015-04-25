package fr.PunKeel.BungeeGuard.Commands;

import fr.PunKeel.BungeeGuard.Main;
import fr.PunKeel.BungeeGuard.Models.BungeeMute;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import java.util.UUID;

public class CommandSay extends Command {
    Main plugin;

    public CommandSay(Main plugin) {
        super("say", "bungee.say");
        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length == 0) {
            sender.sendMessage(TextComponent.fromLegacyText(ChatColor.GREEN + "Usage: /say <message> " + ChatColor.WHITE + "- Envoi d'un message sur tous les serveurs"));
            return;
        }
        UUID uuid = (sender instanceof ProxiedPlayer) ? ((ProxiedPlayer) sender).getUniqueId() : null;

        BungeeMute mute = plugin.getSanctionManager().findMute(uuid);
        if (mute != null) {
            sender.sendMessage(TextComponent.fromLegacyText(mute.getMuteMessage()));
            return;
        }

        if (args.length > 0) {
            String msg = "";
            for (String m : args)
                msg += m + " ";
            Main.getMB().broadcastServers("*", msg, uuid);
            Main.getMB().notifyStaff("[" + Main.getMB().getServerId() + "] " + sender.getName() + ": /say " + msg);
        }
    }
}