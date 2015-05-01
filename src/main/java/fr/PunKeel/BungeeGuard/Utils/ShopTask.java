package fr.PunKeel.BungeeGuard.Utils;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.google.common.reflect.TypeToken;
import fr.PunKeel.BungeeGuard.Main;
import fr.PunKeel.BungeeGuard.Models.BungeeCommandModel;
import fr.PunKeel.BungeeGuard.Persistence.VoidRunner;
import net.md_5.bungee.api.config.ServerInfo;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ShopTask implements Runnable {
    private final Main plugin;
    private final Type mapType = new TypeToken<List<Map<String, Object>>>() {
    }.getType();
    private final Map<String, Action> actions = new ImmutableMap.Builder<String, Action>()
            .put("broadcast", new Action() {
                @Override
                public void run(Map<String, Object> params) {
                    Preconditions.checkArgument(params.containsKey("message"));
                    String message = (String) params.get("message");
                    List<String> serversList = new ArrayList<>();

                    for (ServerInfo server : Main.getServerManager().getOnlineLobbies()) {
                        serversList.add(server.getName());
                    }
                    Main.getMB().broadcastServers(serversList, message);
                }
            })
            .put("say", new Action() {
                @Override
                public void run(Map<String, Object> params) {
                    Preconditions.checkArgument(params.containsKey("message"));
                    String message = (String) params.get("message");
                    Main.getMB().broadcastServers("*", message);
                }
            })
            .put("add_rank", new Action() {
                @Override
                public void run(Map<String, Object> params) {
                    Preconditions.checkArgument(params.containsKey("pseudo"));
                    Preconditions.checkArgument(params.containsKey("duration"));
                    Preconditions.checkArgument(params.containsKey("rank"));
                    String pseudo = (String) params.get("pseudo");
                    String duration = (String) params.get("duration");
                    String rank = (String) params.get("rank");
                    plugin.getProxy().getPluginManager().dispatchCommand(plugin.getProxy().getConsole(), "user " + pseudo + " add " + rank + " " + duration);

                }
            })
            .put("wallet", new Action() {
                @Override
                public void run(Map<String, Object> params) {
                    Preconditions.checkArgument(params.containsKey("pseudo"));
                    String pseudo = (String) params.get("pseudo");
                    UUID uuid = Main.getMB().getUuidFromName(pseudo, true);

                    if (uuid == null)
                        return;

                    if (params.containsKey("set")) {
                        double amount = (double) params.get("set");
                        plugin.getWalletManager().setBalance(uuid, amount);
                    }

                    if (params.containsKey("add")) {
                        double amount = (double) params.get("add");
                        plugin.getWalletManager().addToBalance(uuid, amount);
                    }
                }
            })
            .build();

    public ShopTask(Main plugin) {
        this.plugin = plugin;
    }

    public void run() {
        plugin.executePersistenceRunnable(new VoidRunner() {
            @Override
            protected void run() {
                List<BungeeCommandModel> shopActions = BungeeCommandModel.findAll();
                for (BungeeCommandModel action : shopActions) {
                    String todo = action.getAction();
                    String conditions = action.getCondition();
                    List<Map<String, Object>> tasks = Main.getGson().fromJson(todo, mapType);
                    if (checkConditions(conditions) && action.delete()) {
                        for (Map<String, Object> task : tasks) {
                            if (task.containsKey("action")) {
                                String taskAction = (String) task.get("action");
                                if (actions.containsKey(taskAction)) {
                                    actions.get(taskAction).run(task);
                                } else {
                                    Main.logger().severe("[SHOP] '" + action + "' inconnue ...");
                                }
                            } else {
                                Main.logger().severe("[SHOP] No Action key");
                            }
                        }
                    }
                }
            }
        });
    }

    private boolean checkConditions(String rawCondition) {
        return true;
    }


    abstract class Action {
        public abstract void run(Map<String, Object> params);
    }

    abstract class Condition {
        public abstract boolean check(Map<String, Object> params);
    }
}
