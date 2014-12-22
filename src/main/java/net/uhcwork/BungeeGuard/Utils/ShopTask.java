package net.uhcwork.BungeeGuard.Utils;

import com.google.common.base.Preconditions;
import com.google.common.reflect.TypeToken;
import net.md_5.bungee.api.config.ServerInfo;
import net.uhcwork.BungeeGuard.Main;
import net.uhcwork.BungeeGuard.Models.BungeeCommandModel;
import net.uhcwork.BungeeGuard.Persistence.VoidRunner;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ShopTask implements Runnable {
    private final Main plugin;

    public ShopTask(Main plugin) {
        this.plugin = plugin;
    }

    public void run() {
        plugin.executePersistenceRunnable(new VoidRunner() {
            @Override
            protected void run() {
                List<BungeeCommandModel> actions = BungeeCommandModel.findAll();
                for (BungeeCommandModel action : actions) {
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
                    case "add_rank":
                        addRank(params);
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
        List<String> serversList = new ArrayList<>();

        for (ServerInfo server : Main.getServerManager().getOnlineLobbies()) {
            serversList.add(server.getName());
        }
        Main.getMB().broadcastServers(serversList, message);
    }


    private void say(Map<String, Object> params) {
        Preconditions.checkArgument(params.containsKey("message"));
        String message = (String) params.get("message");
        Main.getMB().broadcastServers("*", message);

    }

    private void addRank(Map<String, Object> params) {
        Preconditions.checkArgument(params.containsKey("pseudo"));
        Preconditions.checkArgument(params.containsKey("duration"));
        Preconditions.checkArgument(params.containsKey("rank"));
        String pseudo = (String) params.get("pseudo");
        String duration = (String) params.get("time");
        String rank = (String) params.get("rank");
        plugin.getProxy().getPluginManager().dispatchCommand(plugin.getProxy().getConsole(), "user " + pseudo + " add " + rank + " " + duration);
    }

    private void wallet(Map<String, Object> params) {
        Preconditions.checkArgument(params.containsKey("pseudo"));
        String pseudo = (String) params.get("pseudo");
        if (params.containsKey("set")) {
            double amount = (double) params.get("set");
            plugin.getProxy().getPluginManager().dispatchCommand(plugin.getProxy().getConsole(), "wallet set " + pseudo + " " + (int) amount);
        }
        if (params.containsKey("add")) {
            double amount = (double) params.get("add");
            plugin.getProxy().getPluginManager().dispatchCommand(plugin.getProxy().getConsole(), "wallet add " + pseudo + " " + (int) amount);
        }
    }
}
