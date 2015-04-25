package fr.PunKeel.BungeeGuard.MultiBungee.PubSub;

import com.google.common.collect.ObjectArrays;
import fr.PunKeel.BungeeGuard.Main;
import fr.PunKeel.BungeeGuard.MultiBungee.PubSubHandler;
import fr.PunKeel.BungeeGuard.MultiBungee.PubSubMessageEvent;
import fr.PunKeel.BungeeGuard.Permissions.Permissions;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.*;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.UUID;

import static fr.PunKeel.BungeeGuard.Utils.PrettyLinkComponent.fromLegacyText;

public class MessageHandler {
    @PubSubHandler("message")
    public static void message(PubSubMessageEvent e) {
        UUID uuid = UUID.fromString(e.getArg(0));
        String message = e.getArg(1);
        ProxiedPlayer p = ProxyServer.getInstance().getPlayer(uuid);
        if (p == null)
            return;
        p.sendMessage(fromLegacyText(message));
    }

    @PubSubHandler("privateMessage")
    public static void privateMessage(Main plugin, PubSubMessageEvent e) {
        UUID senderUUID = UUID.fromString(e.getArg(0));
        String sender = Main.getMB().getNameFromUuid(senderUUID);
        UUID receiverUUID = UUID.fromString(e.getArg(1));
        String receiver = Main.getMB().getNameFromUuid(receiverUUID);
        String message = e.getArg(2);
        BaseComponent[] contenu;

        if (Permissions.hasPerm(senderUUID, "bungeeguard.colormsg")) {
            contenu = fromLegacyText(ChatColor.translateAlternateColorCodes('&', message));
        } else {
            contenu = new BaseComponent[]{new TextComponent(message)};
        }


        ClickEvent clickEventReceiver = new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/w " + sender + " ");
        ClickEvent clickEventSender = new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/w " + receiver + " ");

        HoverEvent hoverEvent = new HoverEvent(HoverEvent.Action.SHOW_TEXT, fromLegacyText(ChatColor.DARK_GREEN + "Cliquez ici pour répondre"));

        BaseComponent[] mpReceiver = new ComponentBuilder("[").color(ChatColor.GRAY).event(clickEventReceiver).event(hoverEvent)
                .append(sender).color(ChatColor.GREEN).event(clickEventReceiver).event(hoverEvent)
                .append(" ➠ ").color(ChatColor.GRAY).event(clickEventReceiver).event(hoverEvent)
                .append("Moi").color(ChatColor.GREEN).event(clickEventReceiver).event(hoverEvent)
                .append("]").color(ChatColor.GRAY).event(clickEventReceiver).event(hoverEvent)
                .append(" ").event((ClickEvent) null).event((HoverEvent) null)
                .create();


        BaseComponent[] mpSender = new ComponentBuilder("[").color(ChatColor.GRAY).event(clickEventSender).event(hoverEvent)
                .append("Moi").color(ChatColor.GREEN).event(clickEventSender).event(hoverEvent)
                .append(" ➠ ").color(ChatColor.GRAY).event(clickEventSender).event(hoverEvent)
                .append(receiver).color(ChatColor.GREEN).event(clickEventSender).event(hoverEvent)
                .append("]").color(ChatColor.GRAY).event(clickEventSender).event(hoverEvent)
                .append(" ").event((HoverEvent) null).event((ClickEvent) null)
                .create();


        ProxiedPlayer player = ProxyServer.getInstance().getPlayer(receiverUUID);

        if (player != null) {
            player.sendMessage(ObjectArrays.concat(mpReceiver, contenu, BaseComponent.class));
        }

        player = ProxyServer.getInstance().getPlayer(senderUUID);

        if (player != null) {
            player.sendMessage(ObjectArrays.concat(mpSender, contenu, BaseComponent.class));
        }


        plugin.setReply(receiverUUID, Main.getMB().getUuidFromName(sender));
        plugin.setReply(Main.getMB().getUuidFromName(sender), receiverUUID);

        ProxiedPlayer admin;
        BaseComponent[] mpSpy = new ComponentBuilder("[").color(ChatColor.GRAY)
                .append("SPY").color(ChatColor.RED)
                .append("] ").color(ChatColor.GRAY)
                .append(sender).color(ChatColor.AQUA)
                .append(" > /msg ").color(ChatColor.WHITE)
                .append(receiver + " ")
                .create();
        mpSpy = ObjectArrays.concat(mpSpy, contenu, BaseComponent.class);
        for (UUID uuid : plugin.getSpies()) {
            try {
                admin = ProxyServer.getInstance().getPlayer(uuid);
                admin.sendMessage(mpSpy);
            } catch (Exception ignored) {
            }
        }
    }
}
