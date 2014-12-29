package net.uhcwork.BungeeGuard.MultiBungee.PubSub;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.google.gson.reflect.TypeToken;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.uhcwork.BungeeGuard.Main;
import net.uhcwork.BungeeGuard.MultiBungee.PubSubHandler;
import net.uhcwork.BungeeGuard.MultiBungee.PubSubMessageEvent;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Map;
import java.util.UUID;

public class IgnoreHandler {
    @PubSubHandler("ignore")
    public static void ignore(Main plugin, PubSubMessageEvent e) {
        UUID joueur = UUID.fromString(e.getArg(0));
        String action = e.getArg(1);
        UUID toIgnore = null;
        if (!e.getArg(2).equals("*")) {
            toIgnore = UUID.fromString(e.getArg(2));
            ProxiedPlayer p = ProxyServer.getInstance().getPlayer(joueur);
            if (p != null && p.getServer().getInfo().getName().startsWith("lobby")) {
                ByteArrayDataOutput out = ByteStreams.newDataOutput();
                out.writeUTF("ignore");
                out.writeBoolean(action.equals("+"));
                byte[] data = out.toByteArray();
                p.sendData("UHCGames", data);
            }
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
        Type type = new TypeToken<Map<UUID, Collection<UUID>>>() {
        }.getType();
        System.out.println("[MB] Ignores: received" + data);
        plugin.getIgnoreManager().setIgnoreList(Main.getGson().<Map<UUID, Collection<UUID>>>fromJson(data, type));
    }

    @PubSubHandler("@ignoresRequest")
    public static void ignoresRequest(Main plugin, PubSubMessageEvent e) {
        String serveur = e.getArg(0);
        Main.getMB().replyIgnores(serveur, Main.getGson().toJson(plugin.getIgnoreManager().getIgnoreList().asMap()));
    }
}
