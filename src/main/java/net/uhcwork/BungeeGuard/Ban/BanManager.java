package net.uhcwork.BungeeGuard.Ban;

import net.uhcwork.BungeeGuard.Main;
import net.uhcwork.BungeeGuard.Models.BungeeBan;
import org.javalite.activejdbc.LazyList;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * Part of net.uhcwork.BungeeGuard.Ban (bungeeguard)
 * Date: 20/09/2014
 * Time: 22:40
 * May be open-source & be sold (by mguerreiro, of course !)
 */
public class BanManager {
    public List<BungeeBan> banList = new ArrayList<>();

    public BanManager(Main plugin) {
    }

    public void loadBans() {
        LazyList<BungeeBan> bans = BungeeBan.where("status = 1");
        banList.addAll(Arrays.asList(bans.toArray(new BungeeBan[bans.size()])));
    }

    public BungeeBan findBan(UUID uuid) {
        for (BungeeBan ban : banList) {
            if (ban.getBannedUUID().equals(uuid)) {
                return ban;
            }
        }
        return null;
    }

    public void removeBan(BungeeBan ban) {
        if (ban == null)
            return;
        if (banList.contains(ban))
            banList.remove(ban);
    }

    public BungeeBan ban(UUID bannedUUID, String bannedName, long bannedUntilTime, String reason, String adminName, UUID adminUUID, boolean saveToBdd) {
        Main.getDb();
        BungeeBan ban;
        ban = findBan(bannedUUID);
        unban(ban, adminName, "ReBan", saveToBdd);

        ban = new BungeeBan();
        ban.setBannedUUID(bannedUUID.toString());
        ban.setBannedName(bannedName);
        ban.setAdminName(adminName);
        ban.setAdminUUID(adminUUID.toString());
        ban.setReason(reason);
        ban.setBannedUntil(bannedUntilTime);
        ban.setStatus(1);
        banList.add(ban);
        if (saveToBdd)
            ban.saveIt();
        return ban;
    }

    public void unban(BungeeBan ban, String adminName, String reason, boolean saveToBdd) {
        Main.getDb();
        if (ban == null)
            return;
        ban.setUnbanReason(reason);
        ban.setUnbanAdminName(adminName);
        ban.setUnbanTime(System.currentTimeMillis());
        ban.setStatus(0);
        removeBan(ban);
        if (saveToBdd)
            ban.saveIt();
    }
}
