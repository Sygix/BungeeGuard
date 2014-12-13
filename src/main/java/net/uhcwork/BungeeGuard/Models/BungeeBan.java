package net.uhcwork.BungeeGuard.Models;

import net.md_5.bungee.api.ChatColor;
import net.uhcwork.BungeeGuard.BungeeGuardUtils;
import net.uhcwork.BungeeGuard.Main;
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
        String reason = (getReason().isEmpty()) ? "." : " avec la raison:\n" + getReason();
        String duration = isDefBanned() ? "définitivement" : "pendant " + ChatColor.AQUA + BungeeGuardUtils.getDuration(getUntilTimestamp()) + ChatColor.RED;
        return ChatColor.RED + "Vous avez été banni " + ChatColor.RED + duration + reason;
    }

    public String getAdminNotification() {
        String reason = (getReason().isEmpty()) ? "." : " avec la raison " + getReason();
        String duration = isDefBanned() ? " définitivement" : " pendant " + ChatColor.AQUA + BungeeGuardUtils.getDuration(getUntilTimestamp()) + ChatColor.RED;
        return Main.ADMIN_TAG + ChatColor.AQUA + getAdminName() + ChatColor.RED + " a banni " + ChatColor.GREEN + getBannedName() + ChatColor.RED + duration + reason;
    }

    public Long getUnban() {
        return getLong("unban");
    }
}
