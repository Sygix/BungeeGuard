package net.uhcwork.BungeeGuard.Wallet;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.uhcwork.BungeeGuard.Main;
import net.uhcwork.BungeeGuard.Models.WalletAccountModel;
import net.uhcwork.BungeeGuard.MultiBungee.MultiBungee;

import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * Part of net.uhcwork.BungeeGuard.Wallet (bungeeguard)
 * Date: 27/09/2014
 * Time: 21:35
 * May be open-source & be sold (by mguerreiro, of course !)
 */
public class WalletManager {

    private final Main plugin;
    private MultiBungee MB;
    private LoadingCache<UUID, WalletAccountModel> walletsCache = CacheBuilder.newBuilder()
            .maximumSize(1000)
            .expireAfterWrite(60, TimeUnit.SECONDS)
            .build(new CacheLoader<UUID, WalletAccountModel>() {
                @Override
                public WalletAccountModel load(UUID u) throws Exception {
                    Main.getDb();
                    System.out.println("" + u);
                    WalletAccountModel WAM = WalletAccountModel.findFirst("uuid = ?", "" + u);
                    if (WAM == null)
                        return createAccount(u);
                    return WAM;
                }
            });

    public WalletManager(Main main) {
        MB = Main.getMB();

        this.plugin = main;
    }

    public WalletAccountModel getAccount(UUID u) {
        if (u == null)
            return null;
        try {
            return walletsCache.get(u);
        } catch (ExecutionException e) {
            e.printStackTrace(); // So sad.
        }
        return null;
    }

    WalletAccountModel createAccount(UUID u) {
        WalletAccountModel WAM = new WalletAccountModel();
        WAM.setUUID(u);
        WAM.setMoney(0);
        WAM.setActive(true);
        WAM.setPlayerName(MB.getNameFromUuid(u));
        WAM.saveIt();
        walletsCache.put(u, WAM);
        return WAM;
    }

    int getBalance(ProxiedPlayer p) {
        return getBalance(p.getUniqueId());
    }

    int getBalance(UUID uniqueId) {
        return getAccount(uniqueId).getMoney();
    }

    void setBalance(UUID uuid, int balance) {
        WalletAccountModel WAM = getAccount(uuid);
        WAM.setMoney(balance);
        WAM.saveIt();
    }

    public void addToBalance(UUID uuid, int amount) {
        WalletAccountModel WAM = getAccount(uuid);
        WAM.setMoney(WAM.getMoney() + amount);
        WAM.saveIt();
    }

    public boolean isActive(UUID uuid) {
        return getAccount(uuid).isActive();
    }

    public void setInactive(UUID uuid) {
        setActive(uuid, false);
    }

    public void setActive(UUID uuid) {
        setActive(uuid, true);
    }

    public void setActive(UUID uuid, boolean active) {
        WalletAccountModel WAM = getAccount(uuid);
        WAM.setActive(active);
        WAM.saveIt();
    }
}
