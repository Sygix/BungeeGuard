package fr.PunKeel.BungeeGuard.Commands;

import fr.PunKeel.BungeeGuard.Main;
import fr.PunKeel.BungeeGuard.Models.BungeeFriendVIP;
import fr.PunKeel.BungeeGuard.Persistence.VoidRunner;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import java.sql.Timestamp;
import java.util.UUID;

public class CommandFriendVIP extends Command {
    private static final BaseComponent[] already_given = TextComponent.fromLegacyText(ChatColor.RED + "Vous avez déjà offert un VIP il y a moins d'une heure.");
    private static final BaseComponent[] unknown_user = TextComponent.fromLegacyText(ChatColor.RED + "Le nom spécifié semble incorrect.");
    private static final BaseComponent[] vip_ajoute = TextComponent.fromLegacyText(ChatColor.GREEN + "*Pof* Une heure de VIP vient d'être offerte !");
    Main plugin;
    int cooldown = 3600; // temps en secondes entre deux utilisations

    public CommandFriendVIP(Main plugin) {
        super("friendvip", "bungee.command.friendvip");
        this.plugin = plugin;
    }

    @Override
    public void execute(final CommandSender sender, final String[] args) {
        if (args.length != 1) {
            sender.sendMessage(TextComponent.fromLegacyText(ChatColor.RED + "Usage: /friendvip <pseudo>"));
            return;
        }
        if (!(sender instanceof ProxiedPlayer))
            return;

        final ProxiedPlayer p = (ProxiedPlayer) sender;

        plugin.executePersistenceRunnable(new VoidRunner() {
            @Override
            protected void run() {
                Timestamp now = new Timestamp(System.currentTimeMillis() - 1000 * cooldown);
                long count = BungeeFriendVIP.count("sender = ? AND created_at > ?", "" + p.getUniqueId(), now);
                if (count != 0) {
                    sender.sendMessage(already_given);
                    return;
                }
                String playerName = args[0];
                UUID uuid = Main.getMB().getUuidFromName(playerName);
                if (uuid == null) {
                    sender.sendMessage(unknown_user);
                    return;
                }
                sender.sendMessage(vip_ajoute);
                Main.getMB().sendPlayerMessage(uuid, ChatColor.GOLD + p.getName() + ChatColor.GREEN + " vient de vous offrir une heure de " + ChatColor.GOLD + "VIP" + ChatColor.GREEN + " !");
                plugin.getProxy().getPluginManager().dispatchCommand(plugin.getProxy().getConsole(), "user " + playerName + " add vip 1h");
                BungeeFriendVIP bf = new BungeeFriendVIP();
                bf.setSender(p.getUniqueId());
                bf.setRecipient(uuid);
                bf.saveIt();
            }
        });
    }
}