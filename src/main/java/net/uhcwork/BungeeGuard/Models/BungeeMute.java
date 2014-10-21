package net.uhcwork.BungeeGuard.Models;

import net.md_5.bungee.api.ChatColor;
import net.uhcwork.BungeeGuard.BungeeGuardUtils;
import net.uhcwork.BungeeGuard.Main;
import org.javalite.activejdbc.Model;
import org.javalite.activejdbc.annotations.Table;

import java.util.UUID;

/**
 * Part of net.uhcwork.BungeeGuard.Models (bungeeguard)
 * Date: 20/09/2014
 * Time: 21:34
 * May be open-source & be sold (by mguerreiro, of course !)
 */
@Table("BungeeGuard_Mute")
public class BungeeMute extends Model {

    public void setAdminUUID(String adminUUID) {
        setString("uuidAdmin", adminUUID);
    }

    public void setMutedUntil(long mutedUntilTime) {
        setLong("mute", mutedUntilTime);
    }

    public UUID getMutedUUID() {
        return UUID.fromString(getString("uuidMute"));
    }

    public void setMutedUUID(String mutedUUID) {
        setString("uuidMute", mutedUUID);
    }

    public void setUnmuteReason(String reason) {
        setString("unmuteReason", reason);
    }

    public void setUnmuteTime(long unmuteTime) {
        setLong("unmute", unmuteTime);
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
        return getLong("mute");
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

    public boolean isMute() {
        return getUntilTimestamp() > System.currentTimeMillis();
    }

    public void setUnmuteAdminName(String adminName) {
        setString("unmuteName", adminName);
    }

    public String getMuteMessage() {
        String reason = (getReason().isEmpty()) ? "." : " avec la raison: " + ChatColor.AQUA + getReason() + ChatColor.RED + ".";
        String duration = "pendant " + ChatColor.AQUA + BungeeGuardUtils.getDuration(getUntilTimestamp()) + ChatColor.RED;
        return ChatColor.RED + "Vous avez été mute " + ChatColor.RED + duration + reason;
    }

    public String getAdminNotification() {
        String reason = (getReason().isEmpty()) ? "." : " avec la raison: " + ChatColor.AQUA + getReason() + ChatColor.RED + ".";
        String duration = "pendant " + ChatColor.AQUA + BungeeGuardUtils.getDuration(getUntilTimestamp()) + ChatColor.RED;
        return Main.ADMIN_TAG + ChatColor.AQUA + getAdminName() + " a mute " + ChatColor.GREEN + getMutedName() + ChatColor.RED + duration + reason;
    }

    public String getMutedName() {
        return getString("nameMute");
    }

    public void setMutedName(String mutedName) {
        setString("nameMute", mutedName);
    }
}
