package fr.greenns.BungeeGuard.Models;

import org.javalite.activejdbc.Model;
import org.javalite.activejdbc.annotations.Table;

import java.util.UUID;

/**
 * Part of fr.greenns.BungeeGuard.Models (bungeeguard)
 * Date: 20/09/2014
 * Time: 21:34
 * May be open-source & be sold (by mguerreiro, of course !)
 */
@Table("BungeeGuard_Mute")
public class BungeeMute extends Model {

    public void setMutedUUID(String mutedUUID) {
        setString("uuidMute", mutedUUID);
    }

    public void setMutedName(String mutedName) {
        setString("nameMute", mutedName);
    }

    public void setAdminName(String adminName) {
        setString("nameAdmin", adminName);
    }

    public void setAdminUUID(String adminUUID) {
        setString("uuidAdmin", adminUUID);
    }

    public void setReason(String reason) {
        setString("reason", reason);
    }

    public void setMutedUntil(long mutedUntilTime) {
        setLong("mute", mutedUntilTime);
    }

    public UUID getMutedUUID() {
        return UUID.fromString(getString("uuidMute"));
    }

    public void setUnmuteReason(String reason) {
        setString("unmuteReason", reason);
    }

    public void setUnmuteTime(long unmuteTime) {
        setLong("unmute", unmuteTime);
    }

    public void setStatus(int status) {
        setBoolean("status", status);
    }

    public String getReason() {
        return getString("reason");
    }

    public long getUntilTimestamp() {
        return getLong("mute");
    }

    public String getAdminName() {
        return getString("nameAdmin");
    }

    public int getStatus() {
        return getInteger("status");
    }

    public boolean isMute() {
        return getUntilTimestamp() > System.currentTimeMillis();
    }

    public void setUnmuteAdminName(String adminName) {
        setString("unmuteName", adminName);
    }
}
