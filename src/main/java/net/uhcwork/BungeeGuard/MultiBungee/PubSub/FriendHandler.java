package net.uhcwork.BungeeGuard.MultiBungee.PubSub;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.uhcwork.BungeeGuard.Main;
import net.uhcwork.BungeeGuard.Managers.FriendManager;
import net.uhcwork.BungeeGuard.MultiBungee.PubSubHandler;
import net.uhcwork.BungeeGuard.MultiBungee.PubSubMessageEvent;
import net.uhcwork.BungeeGuard.Utils.MyBuilder;

import java.util.UUID;

public class FriendHandler {
    @PubSubHandler("addfriend")
    public static void onFriendAdd(Main plugin, PubSubMessageEvent e) {
        UUID userA = UUID.fromString(e.getArg(0));
        UUID userB = UUID.fromString(e.getArg(1));
        plugin.getFriendManager().addFriend(userA, userB, false);

        ProxiedPlayer p = plugin.getProxy().getPlayer(userB);
        String userNameA = Main.getMB().getNameFromUuid(userA);
        p.sendMessage(TextComponent.fromLegacyText("[" + ChatColor.RED + "♥" + ChatColor.WHITE + "] " + ChatColor.GREEN + userNameA + ChatColor.YELLOW + " vient de vous ajouter à sa liste d'amis."));
        if (plugin.getFriendManager().getFriendship(userA, userB).equals(FriendManager.STATE.MUTUAL))
            return;
        MyBuilder mb = new MyBuilder(ChatColor.YELLOW + "Ajoutez le à votre tour ! ");
        mb.append(ChatColor.AQUA + "/friend add " + ChatColor.ITALIC + userNameA);
        mb.event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/friend + " + userNameA));
        p.sendMessage(mb.create());
    }

    @PubSubHandler("delfriend")
    public static void onFriendDel(Main plugin, PubSubMessageEvent e) {
        UUID userA = UUID.fromString(e.getArg(0));
        UUID userB = UUID.fromString(e.getArg(1));
        System.out.println("-" + userA + ", " + userB);
        plugin.getFriendManager().removeFriend(userA, userB, false);

    }
}
