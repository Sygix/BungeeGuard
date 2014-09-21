package fr.greenns.BungeeGuard.Mute;

import fr.greenns.BungeeGuard.Main;
import fr.greenns.BungeeGuard.Models.BungeeMute;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * Part of fr.greenns.BungeeGuard.Mute (bungeeguard)
 * Date: 20/09/2014
 * Time: 22:42
 * May be open-source & be sold (by mguerreiro, of course !)
 */
public class MuteManager {
    private final Main plugin;
    private List<BungeeMute> muteList = new ArrayList<>();

    public MuteManager(Main plugin) {
        this.plugin = plugin;
    }

    public void loadMutes() {
        muteList.addAll(Arrays.asList(BungeeMute.where("status = 1").toArray(new BungeeMute[]{})));

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
        Main.getDb();
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
            mute.saveIt();
        muteList.add(mute);
        return mute;
    }

    public void unmute(BungeeMute mute, String adminName, String reason, boolean saveToBdd) {
        if (mute == null)
            return;
        Main.getDb();
        mute.setUnmuteAdminName(adminName);
        mute.setUnmuteReason(reason);
        mute.setUnmuteTime(System.currentTimeMillis());
        mute.setStatus(0);
        removeMute(mute);
        if (saveToBdd)
            mute.saveIt();
    }

}
