package net.uhcwork.BungeeGuard.Utils;

import com.google.common.base.Preconditions;
import com.google.common.reflect.TypeToken;
import net.uhcwork.BungeeGuard.Main;
import net.uhcwork.BungeeGuard.Managers.LobbyManager;
import net.uhcwork.BungeeGuard.Models.ShopActionModel;
import net.uhcwork.BungeeGuard.Persistence.VoidRunner;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Part of net.uhcwork.BungeeGuard.Utils (BungeeGuard)
 * Date: 22/10/2014
 * Time: 24:25
 * May be open-source & be sold (by mguerreiro, of course !)
 */
public class ShopTask implements Runnable {
    Main plugin;

    public ShopTask(Main plugin) {
        this.plugin = plugin;
    }

    public void run() {
        plugin.executePersistenceRunnable(new VoidRunner() {
            @Override
            protected void run() {
                List<ShopActionModel> actions = ShopActionModel.findAll();
                for (ShopActionModel action : actions) {
                    String todo = action.getAction();
                    if (action.delete()) {
                        doTask(todo);
                    }
                }
            }
        });
    }

    private void doTask(String todo) {
        Type mapType = new TypeToken<List<Map<String, Object>>>() {
        }.getType();
        List<Map<String, Object>> actions = Main.getGson().fromJson(todo, mapType);
        for (Map<String, Object> params : actions) {
            if (params.containsKey("action")) {
                String action = (String) params.get("action");
                switch (action) {
                    case "broadcast":
                        broadcast(params);
                        break;
                    case "vip":
                        vip(params);
                        break;
                    case "say":
                        say(params);
                        break;
                    case "wallet":
                        wallet(params);
                        break;
                    default:
                        System.out.println("[SHOP] '" + action + "' inconnue ...");
                }
            } else {
                System.out.println("No Action key");
            }
        }
    }

    private void broadcast(Map<String, Object> params) {
        Preconditions.checkArgument(params.containsKey("message"));
        String message = (String) params.get("message");
        System.out.println("[SHOP] Broadcast lobbies: " + message);
        List<String> serversList = new ArrayList<>();

        for (LobbyManager.Lobby server : plugin.getLM().getLobbies()) {
            if (server == null || !server.isOnline())
                continue;
            serversList.add(server.getName());
        }
        Main.getMB().broadcastServers(serversList, message);
    }


    private void say(Map<String, Object> params) {
        Preconditions.checkArgument(params.containsKey("message"));
        String message = (String) params.get("message");
        System.out.println("[SHOP] Broadcast *: " + message);
        Main.getMB().broadcastServers("*", message);

    }

    private void vip(Map<String, Object> params) {
        Preconditions.checkArgument(params.containsKey("pseudo"));
        Preconditions.checkArgument(params.containsKey("time"));
        String pseudo = (String) params.get("pseudo");
        String duration = (String) params.get("time");
        System.out.println("[SHOP] Add VIP for " + pseudo + " (" + duration + ")");
        plugin.getProxy().getPluginManager().dispatchCommand(plugin.getProxy().getConsole(), "user " + pseudo + " add vip " + duration);
    }

    private void wallet(Map<String, Object> params) {
        Preconditions.checkArgument(params.containsKey("pseudo"));
        String pseudo = (String) params.get("pseudo");
        if (params.containsKey("set")) {
            double amount = (double) params.get("set");
            System.out.println("[SHOP] Set " + amount + " coins to " + pseudo);
            plugin.getProxy().getPluginManager().dispatchCommand(plugin.getProxy().getConsole(), "wallet set " + pseudo + " " + (int) amount);
        }
        if (params.containsKey("add")) {
            double amount = (double) params.get("add");
            System.out.println("[SHOP] Add " + amount + " coins to " + pseudo);
            plugin.getProxy().getPluginManager().dispatchCommand(plugin.getProxy().getConsole(), "wallet add " + pseudo + " " + (int) amount);
        }
    }
}
