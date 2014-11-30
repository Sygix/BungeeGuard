package net.uhcwork.BungeeGuard.MultiBungee.PubSub;

import com.google.gson.reflect.TypeToken;
import net.uhcwork.BungeeGuard.Main;
import net.uhcwork.BungeeGuard.MultiBungee.PubSubHandler;
import net.uhcwork.BungeeGuard.MultiBungee.PubSubMessageEvent;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Part of net.uhcwork.BungeeGuard.MultiBungee.PubSub (bungeeguard)
 * Date: 10/09/2014
 * Time: 17:57
 * May be open-source & be sold (by mguerreiro, of course !)
 */
public class IgnoreHandler {
    @PubSubHandler("ignore")
    public static void ignore(Main plugin, PubSubMessageEvent e) {
        UUID joueur = UUID.fromString(e.getArg(0));
        String action = e.getArg(1);
        UUID toIgnore = null;
        if (!e.getArg(2).equals("*")) {
            toIgnore = UUID.fromString(e.getArg(2));
        }

        switch (action) {
            case "+":
                plugin.getIgnoreManager().ignore(joueur, toIgnore);
                break;
            case "-":
                plugin.getIgnoreManager().unIgnore(joueur, toIgnore);
                break;
        }
    }

    @PubSubHandler("@ignoresReply")
    public static void ignoresReply(Main plugin, PubSubMessageEvent e) {
        String data = e.getArg(0);
        Type type = new TypeToken<Map<UUID, List<UUID>>>() {
        }.getType();
        System.out.println("[MB] Ignores: received" + data);
        plugin.getIgnoreManager().setIgnoreList(Main.getGson().<Map<UUID, List<UUID>>>fromJson(data, type));
    }

    @PubSubHandler("@ignoresRequest")
    public static void ignoresRequest(Main plugin, PubSubMessageEvent e) {
        String serveur = e.getArg(0);
        Main.getMB().replyIgnores(serveur, Main.getGson().toJson(plugin.getIgnoreManager().getIgnoreList()));
    }
}
