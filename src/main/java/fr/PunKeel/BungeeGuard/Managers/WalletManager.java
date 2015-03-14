package fr.PunKeel.BungeeGuard.Managers;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import fr.PunKeel.BungeeGuard.Main;
import fr.PunKeel.BungeeGuard.Models.WalletModel;
import fr.PunKeel.BungeeGuard.MultiBungee.MultiBungee;
import fr.PunKeel.BungeeGuard.Persistence.SaveRunner;

import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class WalletManager {

    private final Main plugin;
    private final MultiBungee MB;
    private final LoadingCache<UUID, WalletModel> walletsCache = CacheBuilder.newBuilder()
            .maximumSize(1000)
            .expireAfterWrite(30, TimeUnit.SECONDS)
            .build(new CacheLoader<UUID, WalletModel>() {
                @Override
                public WalletModel load(final UUID u) throws Exception {
                    Future<WalletModel> x = plugin.executePersistenceRunnable(new Callable<WalletModel>() {
                        @Override
                        public WalletModel call() {
                            WalletModel WAM = WalletModel.findFirst("uuid = ?", "" + u);
                            if (WAM == null)
                                return createAccount(u);
                            return WAM;
                        }
                    });
                    return x.get();
                }
            });

    public WalletManager(Main main) {
        MB = Main.getMB();
        this.plugin = main;
    }

    public WalletModel getAccount(UUID u) {
        if (u == null)
            return null;
        try {
            return walletsCache.get(u);
        } catch (ExecutionException e) {
            e.printStackTrace(); // So sad.
        }
        return null;
    }

    WalletModel createAccount(UUID u) {
        String userName = MB.getNameFromUuid(u);
        WalletModel WAM = new WalletModel();
        WAM.setUUID(u);
        WAM.setMoney(0);
        plugin.executePersistenceRunnable(new SaveRunner(WAM));
        walletsCache.put(u, WAM);
        return WAM;
    }

    public double getBalance(UUID uniqueId) {
        return getAccount(uniqueId).getMoney();
    }

    @Deprecated
    public void setBalance(UUID uuid, double balance) {
        WalletModel WAM = getAccount(uuid);
        WAM.setMoney(balance);
        plugin.executePersistenceRunnable(new SaveRunner(WAM));
    }

    @Deprecated
    public void addToBalance(UUID uuid, double amount) {
        WalletModel WAM = getAccount(uuid);
        WAM.setMoney(WAM.getMoney() + amount);
        plugin.executePersistenceRunnable(new SaveRunner(WAM));
    }

    public String getDisplayedBalance(UUID u) {
        double balance = getBalance(u);
        return getDisplayedBalance(balance);
    }

    public String getDisplayedBalance(Double balance) {
        // \u00a0 est le code pour le &nbsp;
        return String.format(Locale.FRENCH, "%,.2f", Math.floor(balance * 4) / 4).replaceAll("\u00a0", " ");
    }
}
