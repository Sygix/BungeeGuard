package fr.greenns.BungeeGuard.Party;

import com.google.gson.reflect.TypeToken;
import fr.greenns.BungeeGuard.Main;
import fr.greenns.BungeeGuard.MultiBungee.PubSub.PubSubBase;

import java.lang.reflect.Type;
import java.util.Map;

/**
 * Part of fr.greenns.BungeeGuard (bungeeguard)
 * Date: 10/09/2014
 * Time: 18:21
 * May be open-source & be sold (by mguerreiro, of course !)
 */
public class PartyReplyHandler extends PubSubBase {
    private final Main plugin;

    public PartyReplyHandler(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public void handle(String channel, String message, String[] args) {
        String data = args[0];
        Type type = new TypeToken<Map<String, Party>>() {
        }.getType();

        plugin.getPM().setParties(plugin.gson.<Map<String, Party>>fromJson(data, type));
    }
}
