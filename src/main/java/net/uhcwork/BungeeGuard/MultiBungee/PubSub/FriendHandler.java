package net.uhcwork.BungeeGuard.MultiBungee.PubSub;

import net.uhcwork.BungeeGuard.Main;
import net.uhcwork.BungeeGuard.MultiBungee.PubSubHandler;
import net.uhcwork.BungeeGuard.MultiBungee.PubSubMessageEvent;

import java.util.UUID;

public class FriendHandler {
    @PubSubHandler("addfriend")
    public static void onFriendAdd(Main plugin, PubSubMessageEvent e) {
        UUID userA = UUID.fromString(e.getArg(0));
        UUID userB = UUID.fromString(e.getArg(1));
        System.out.println("-" + userA + ", " + userB);
        plugin.getFriendManager().addFriend(userA, userB, false);
    }

    @PubSubHandler("delfriend")
    public static void onFriendDel(Main plugin, PubSubMessageEvent e) {
        UUID userA = UUID.fromString(e.getArg(0));
        UUID userB = UUID.fromString(e.getArg(1));
        System.out.println("-" + userA + ", " + userB);
        plugin.getFriendManager().removeFriend(userA, userB, false);

    }
}
