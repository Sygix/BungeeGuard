package fr.PunKeel.BungeeGuard.Models;

import fr.PunKeel.BungeeGuard.Main;
import fr.PunKeel.BungeeGuard.Utils.DateUtil;
import net.md_5.bungee.api.ChatColor;
import org.javalite.activejdbc.Model;
import org.javalite.activejdbc.annotations.Table;

import java.util.UUID;

@Table("bungee_mute")
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

    String getReason() {
        return getString("reason");
    }

    public void setReason(String reason) {
        if (reason == null)
            reason = "";
        setString("reason", reason);
    }

    long getUntilTimestamp() {
        return getLong("mute");
    }

    String getAdminName() {
        return getString("nameAdmin");
    }

    public void setAdminName(String adminName) {
        setString("nameAdmin", adminName);
    }

    public void setStatus(int status) {
        setBoolean("status", status);
    }

    public boolean isMute() {
        return getUnmute() == null && getUntilTimestamp() > System.currentTimeMillis();
    }

    private Long getUnmute() {
        return getLong("unmute");
    }

    public void setUnmuteAdminName(String adminName) {
        setString("unmuteName", adminName);
    }

    public String getMuteMessage() {
        return getMuteMessage(System.currentTimeMillis());
    }

    public String getMuteMessage(Long now) {
        String reason = (getReason().isEmpty()) ? "." : " avec la raison: " + ChatColor.AQUA + getReason() + ChatColor.RED + ".";
        return ChatColor.RED + "Vous avez été mute" + ChatColor.RED + getDuration(now) + reason;
    }

    public String getDuration(long now) {
        return " pendant " + ChatColor.AQUA + DateUtil.formatDateDiff(getUntilTimestamp() - now, true) + ChatColor.RED;
    }

    public String getAdminNotification() {
        return getAdminNotification(System.currentTimeMillis());
    }

    public String getAdminNotification(Long now) {
        String reason = (getReason().isEmpty()) ? "." : " avec la raison: " + ChatColor.AQUA + getReason() + ChatColor.RED + ".";
        return Main.ADMIN_TAG + ChatColor.AQUA + getAdminName() + ChatColor.RED + " a mute " + ChatColor.GREEN + getMutedName() + ChatColor.RED + getDuration(now) + reason;
    }

    String getMutedName() {
        return getString("nameMute");
    }

    public void setMutedName(String mutedName) {
        setString("nameMute", mutedName);
    }
}
