package net.uhcwork.BungeeGuard.Models;

import net.md_5.bungee.api.ChatColor;
import net.uhcwork.BungeeGuard.Main;
import net.uhcwork.BungeeGuard.Utils.DateUtil;
import org.javalite.activejdbc.Model;
import org.javalite.activejdbc.annotations.Table;

import java.util.UUID;

@Table("BungeeGuard_Ban")
public class BungeeBan extends Model {
    public void setAdminUUID(String adminUUID) {
        setString("uuidAdmin", adminUUID);
    }

    public void setBannedUntil(long bannedUntilTime) {
        setLong("ban", bannedUntilTime);
    }

    public UUID getBannedUUID() {
        return UUID.fromString(getString("uuidBanned"));
    }

    public void setBannedUUID(String bannedUUID) {
        setString("uuidBanned", bannedUUID);
    }

    public void setUnbanReason(String reason) {
        setString("unbanReason", reason);
    }

    public void setUnbanTime(long unbanTime) {
        setLong("unban", unbanTime);
    }

    public String getReason() {
        return getString("reason");
    }

    public void setReason(String reason) {
        if (reason == null)
            reason = "";
        setString("reason", reason);
    }

    public long getUntilTimestamp() {
        return getLong("ban");
    }

    public String getAdminName() {
        return getString("nameAdmin");
    }

    public void setAdminName(String adminName) {
        setString("nameAdmin", adminName);
    }

    public void setStatus(int status) {
        setBoolean("status", status);
    }

    boolean isDefBanned() {
        return getUntilTimestamp() == -1;
    }

    public boolean isBanned() {
        return getUnban() == null && (isDefBanned() || getUntilTimestamp() > System.currentTimeMillis());
    }

    public void setUnbanAdminName(String adminName) {
        setString("unbanName", adminName);
    }

    String getBannedName() {
        return getString("nameBanned");
    }

    public void setBannedName(String bannedName) {
        setString("nameBanned", bannedName);
    }

    public String getBanMessage() {
        return getBanMessage(System.currentTimeMillis());
    }

    public String getBanMessage(Long now) {
        String reason = (getReason().isEmpty()) ? "." : " avec la raison:\n" + getReason();
        return ChatColor.RED + "Vous avez été banni " + ChatColor.RED + getDuration(now) + reason;
    }

    public String getDuration(Long now) {
        return isDefBanned() ? " définitivement" : " pendant " + ChatColor.AQUA + DateUtil.formatDateDiff(getUntilTimestamp() - now, true) + ChatColor.RED;
    }

    public String getAdminNotification() {
        return getAdminNotification(System.currentTimeMillis());
    }

    public String getAdminNotification(Long now) {
        String reason = (getReason().isEmpty()) ? "." : " avec la raison " + getReason();
        return Main.ADMIN_TAG + ChatColor.AQUA + getAdminName() + ChatColor.RED + " a banni " + ChatColor.GREEN + getBannedName() + ChatColor.RED + getDuration(now) + reason;
    }

    public Long getUnban() {
        return getLong("unban");
    }

    public void setIp(String ip) {
        if (ip == null)
            return;
        setString("ip", ip);
    }
}
