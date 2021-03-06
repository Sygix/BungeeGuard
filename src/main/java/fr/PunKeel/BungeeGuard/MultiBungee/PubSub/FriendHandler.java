package fr.PunKeel.BungeeGuard.MultiBungee.PubSub;

import fr.PunKeel.BungeeGuard.Main;
import fr.PunKeel.BungeeGuard.Managers.FriendManager;
import fr.PunKeel.BungeeGuard.MultiBungee.PubSubHandler;
import fr.PunKeel.BungeeGuard.MultiBungee.PubSubMessageEvent;
import fr.PunKeel.BungeeGuard.Utils.MyBuilder;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.UUID;

public class FriendHandler {
    @PubSubHandler("addfriend")
    public static void onFriendAdd(Main plugin, PubSubMessageEvent e) {
        UUID userA = UUID.fromString(e.getArg(0));
        UUID userB = UUID.fromString(e.getArg(1));
        plugin.getFriendManager().addFriend(userA, userB, false);
        if (plugin.getFriendManager().getFriendship(userA, userB) == FriendManager.STATE.MUTUAL)
            plugin.getPluginMessageManager().sendFriendAdd(userA, userB);

        ProxiedPlayer p = plugin.getProxy().getPlayer(userB);
        if (p == null)
            return;
        String userNameA = Main.getMB().getNameFromUuid(userA);
        p.sendMessage(TextComponent.fromLegacyText(FriendManager.getTAG() + ChatColor.GREEN + userNameA + ChatColor.YELLOW + " vient de vous ajouter à sa liste d'amis."));
        if (plugin.getFriendManager().getFriendship(userA, userB).equals(FriendManager.STATE.MUTUAL))
            return;
        MyBuilder mb = new MyBuilder(ChatColor.YELLOW + "Ajoutez le à votre tour ! ")
                .append(ChatColor.AQUA + "/friend add " + ChatColor.ITALIC + userNameA)
                .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/friends add " + userNameA))
                .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText(ChatColor.GREEN + "Clique ici pour ajouter " + userNameA + " en ami !")))
                .append("");
        p.sendMessage(mb.create());
    }

    @PubSubHandler("delfriend")
    public static void onFriendDel(Main plugin, PubSubMessageEvent e) {
        UUID userA = UUID.fromString(e.getArg(0));
        UUID userB = UUID.fromString(e.getArg(1));
        plugin.getFriendManager().removeFriend(userA, userB, false);
        plugin.getPluginMessageManager().sendFriendRemove(userA, userB);
    }
}
