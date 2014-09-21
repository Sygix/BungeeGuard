package fr.greenns.BungeeGuard.Models;

import org.javalite.activejdbc.Model;
import org.javalite.activejdbc.annotations.Table;

import java.util.UUID;

/**
 * Part of fr.greenns.BungeeGuard.Models (bungeeguard)
 * Date: 20/09/2014
 * Time: 21:19
 * May be open-source & be sold (by mguerreiro, of course !)
 */
@Table("BungeeGuard_Ban")
public class BungeeBan extends Model {
    public void setBannedUUID(String bannedUUID) {
        setString("uuidBanned", bannedUUID);
    }

    public void setBannedName(String bannedName) {
        setString("nameBanned", bannedName);
    }

    public void setAdminName(String adminName) {
        setString("nameAdmin", adminName);
    }

    public void setAdminUUID(String adminUUID) {
        setString("uuidAdmin", adminUUID);
    }

    public void setReason(String reason) {
        if (reason == null)
            reason = "";
        setString("reason", reason);
    }

    public void setBannedUntil(long bannedUntilTime) {
        setLong("ban", bannedUntilTime);
    }

    public UUID getBannedUUID() {
        return UUID.fromString(getString("uuidBanned"));
    }

    public void setUnbanReason(String reason) {
        setString("unbanReason", reason);
    }

    public void setUnbanTime(long unbanTime) {
        setLong("unban", unbanTime);
    }

    public void setStatus(int status) {
        setBoolean("status", status);
    }

    public String getReason() {
        return getString("reason");
    }

    public long getUntilTimestamp() {
        return getLong("ban");
    }

    public String getAdminName() {
        return getString("nameAdmin");
    }

    public int getStatus() {
        return getInteger("status");
    }

    public boolean isDefBanned() {
        return getUntilTimestamp() == -1;
    }

    public boolean isBanned() {
        return getUntilTimestamp() > System.currentTimeMillis();
    }

    public void setUnbanAdminName(String adminName) {
        setString("unbanName", adminName);
    }
}
