package net.uhcwork.BungeeGuard.MultiBungee.PubSub;

import com.google.common.collect.ObjectArrays;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.uhcwork.BungeeGuard.BungeeGuardUtils;
import net.uhcwork.BungeeGuard.Main;
import net.uhcwork.BungeeGuard.utils.Permissions;

import java.util.UUID;

/**
 * Part of net.uhcwork.BungeeGuard.commands
 * Date: 30/08/2014
 * Time: 00:48
 * May be open-source & be sold (by PunKeel, of course !)
 */
public class PrivateMessageHandler extends PubSubBase {

    private Main plugin;

    public PrivateMessageHandler(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public void handle(String channel, String message, String[] args) {
        String sender = args[0];
        UUID receiver = UUID.fromString(args[1]);
        BaseComponent[] contenu;

        if (Permissions.hasPerm(sender, "bungeeguard.colormsg")) {
            contenu = TextComponent.fromLegacyText(args[2]);
        } else {
            contenu = new BaseComponent[]{new TextComponent(args[2])};
        }

        ProxiedPlayer p = ProxyServer.getInstance().getPlayer(receiver);
        BaseComponent[] mp = new ComponentBuilder("[").color(ChatColor.GRAY).append(sender).color(ChatColor.GREEN).append(" ➠ ").color(ChatColor.GRAY).append("Moi").color(ChatColor.GREEN).append("]").color(ChatColor.GRAY).append(" ").create();

        if (p != null) {
            p.sendMessage(ObjectArrays.concat(mp, contenu, BaseComponent.class));
        }

        plugin.setReply(receiver, BungeeGuardUtils.getMB().getUuidFromName(sender));

        ProxiedPlayer admin;
        for (UUID uuid : plugin.getSpies()) {
            try {
                admin = ProxyServer.getInstance().getPlayer(uuid);
                mp = new ComponentBuilder("[").color(ChatColor.GRAY).append("SPY").color(ChatColor.RED).append("] ").color(ChatColor.GRAY).append(sender).append(": /msg ").append(BungeeGuardUtils.getMB().getNameFromUuid(receiver) + " ").create();
                admin.sendMessage(ObjectArrays.concat(mp, contenu, BaseComponent.class));
            } catch (Exception ignored) {
            }
        }
    }
}
