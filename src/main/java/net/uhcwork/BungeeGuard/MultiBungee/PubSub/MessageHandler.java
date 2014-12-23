package net.uhcwork.BungeeGuard.MultiBungee.PubSub;

import com.google.common.collect.ObjectArrays;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.uhcwork.BungeeGuard.Main;
import net.uhcwork.BungeeGuard.MultiBungee.PubSubHandler;
import net.uhcwork.BungeeGuard.MultiBungee.PubSubMessageEvent;
import net.uhcwork.BungeeGuard.Permissions.Permissions;
import net.uhcwork.BungeeGuard.Utils.PrettyLinkComponent;

import java.util.UUID;

public class MessageHandler {
    @PubSubHandler("message")
    public static void message(PubSubMessageEvent e) {
        UUID uuid = UUID.fromString(e.getArg(0));
        String message = e.getArg(1);
        ProxiedPlayer p = ProxyServer.getInstance().getPlayer(uuid);
        if (p == null)
            return;
        p.sendMessage(PrettyLinkComponent.fromLegacyText(message));
    }

    @PubSubHandler("privateMessage")
    public static void privateMessage(Main plugin, PubSubMessageEvent e) {
        String sender = e.getArg(0);
        UUID receiver = UUID.fromString(e.getArg(1));
        String message = e.getArg(2);
        BaseComponent[] contenu;

        if (Permissions.hasPerm(sender, "bungeeguard.colormsg")) {
            contenu = PrettyLinkComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', message));
        } else {
            contenu = new BaseComponent[]{new TextComponent(message)};
        }

        ProxiedPlayer p = ProxyServer.getInstance().getPlayer(receiver);
        BaseComponent[] mp = new ComponentBuilder("[").color(ChatColor.GRAY).append(sender).color(ChatColor.GREEN).append(" ➠ ").color(ChatColor.GRAY).append("Moi").color(ChatColor.GREEN).append("]").color(ChatColor.GRAY).append(" ").create();

        if (p != null) {
            p.sendMessage(ObjectArrays.concat(mp, contenu, BaseComponent.class));
        }

        plugin.setReply(receiver, Main.getMB().getUuidFromName(sender));
        plugin.setReply(Main.getMB().getUuidFromName(sender), receiver);

        ProxiedPlayer admin;
        for (UUID uuid : plugin.getSpies()) {
            try {
                admin = ProxyServer.getInstance().getPlayer(uuid);
                mp = new ComponentBuilder("[").color(ChatColor.GRAY)
                        .append("SPY").color(ChatColor.RED)
                        .append("] ").color(ChatColor.GRAY)
                        .append(sender).color(ChatColor.AQUA)
                        .append(" > /msg ").color(ChatColor.WHITE)
                        .append(Main.getMB().getNameFromUuid(receiver) + " ").create();
                admin.sendMessage(ObjectArrays.concat(mp, contenu, BaseComponent.class));
            } catch (Exception ignored) {
            }
        }
    }
}
