package net.uhcwork.BungeeGuard.Managers;

import net.uhcwork.BungeeGuard.Main;
import net.uhcwork.BungeeGuard.Models.BungeeMute;
import net.uhcwork.BungeeGuard.Persistence.SaveRunner;
import net.uhcwork.BungeeGuard.Persistence.VoidRunner;
import org.javalite.activejdbc.LazyList;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * Part of net.uhcwork.BungeeGuard.Mute (bungeeguard)
 * Date: 20/09/2014
 * Time: 22:42
 * May be open-source & be sold (by mguerreiro, of course !)
 */
public class MuteManager {
    final private List<BungeeMute> muteList = new ArrayList<>();
    Main plugin;

    public MuteManager(Main plugin) {
        this.plugin = plugin;
    }

    public void loadMutes() {
        plugin.executePersistenceRunnable(new VoidRunner() {
            protected void run() {
                LazyList<BungeeMute> mutes = BungeeMute.where("status = 1");
                muteList.addAll(Arrays.asList(mutes.toArray(new BungeeMute[mutes.size()])));
            }
        });
    }

    public BungeeMute findMute(UUID uuid) {
        for (BungeeMute mute : muteList) {
            if (mute.getMutedUUID().equals(uuid)) {
                return mute;
            }
        }
        return null;
    }

    public void removeMute(BungeeMute mute) {
        if (mute == null)
            return;
        if (muteList.contains(mute))
            muteList.remove(mute);
    }

    public BungeeMute mute(UUID mutedUUID, String mutedName, long mutedUntilTime, String reason, String adminName, UUID adminUUID, boolean saveToBdd) {
        BungeeMute mute;
        mute = findMute(mutedUUID);
        unmute(mute, adminName, "ReMute", saveToBdd);
        removeMute(mute);

        mute = new BungeeMute();
        mute.setMutedUUID(mutedUUID.toString());
        mute.setMutedName(mutedName);
        mute.setAdminName(adminName);
        mute.setAdminUUID(adminUUID.toString());
        mute.setReason(reason);
        mute.setMutedUntil(mutedUntilTime);
        mute.setStatus(1);
        if (saveToBdd)
            plugin.executePersistenceRunnable(new SaveRunner(mute));
        muteList.add(mute);
        return mute;
    }

    public void unmute(final BungeeMute mute, String adminName, String reason, boolean saveToBdd) {
        if (mute == null)
            return;
        mute.setUnmuteAdminName(adminName);
        mute.setUnmuteReason(reason);
        mute.setUnmuteTime(System.currentTimeMillis());
        mute.setStatus(0);
        removeMute(mute);
        if (saveToBdd)
            plugin.executePersistenceRunnable(new SaveRunner(mute));
    }

}
