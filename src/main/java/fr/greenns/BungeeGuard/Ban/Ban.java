package fr.greenns.BungeeGuard.Ban;

import fr.greenns.BungeeGuard.Main;

import java.sql.SQLException;
import java.util.UUID;

public class Ban {
    UUID UUID;
    String joueur;
    long untilTimestamp;
    String reason;
    String adminName;
    UUID adminUUID;

    public Ban(UUID UUID, String joueur, long untilTimestamp, String reason, String adminName, UUID adminUUID) {
        this.UUID = UUID;
        this.joueur = joueur;
        this.untilTimestamp = untilTimestamp;
        this.reason = reason;
        this.adminName = adminName;
        this.adminUUID = adminUUID;
        Main.bans.add(this);
    }

    public String getJoueur() {
        return joueur;
    }

    public void setJoueur(String joueur) {
        this.joueur = joueur;
    }

    public String getAdminName() {
        return adminName;
    }

    public void setAdminName(String adminName) {
        this.adminName = adminName;
    }

    public UUID getAdminUUID() {
        return adminUUID;
    }

    public void setAdminUUID(UUID adminUUID) {
        this.adminUUID = adminUUID;
    }

    public UUID getUUID() {
        return UUID;
    }

    public void setUUID(UUID uUID) {
        UUID = uUID;
    }

    public long getUntilTimestamp() {
        return untilTimestamp;
    }

    public void setUntilTimestamp(long untilTimestamp) {
        this.untilTimestamp = untilTimestamp;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public boolean isDefBanned() {
        return (untilTimestamp == -1);
    }

    public boolean isBanned() {
        return (System.currentTimeMillis() <= untilTimestamp);
    }

    /**
     * STATUS 0 = PAS BANNIS
     * 1 = BANNIS
     */

    public void addToBdd() {
        try {
            if (Main.plugin.sql.getConnection().isClosed()) {
                Main.plugin.sql.open();
            }

            if (Main.plugin.sql.getConnection() == null) {
                System.out.println("[MYSQL] Connection error ...");
            }

            Main.plugin.sql.query("UPDATE `BungeeGuard_Ban` SET status='0', unbanReason='ReBan-By-" + getAdminName() + "', unban='" + System.currentTimeMillis() + "' WHERE uuidBanned='" + getUUID() + "' AND status='1'");

            Main.plugin.sql.query("INSERT INTO `BungeeGuard_Ban` "
                    + "(`id`, `nameBanned`, `nameAdmin` , `uuidBanned` , `uuidAdmin` , `ban`, `unban`, `reason`, `unbanReason`, `unbanName`, `status`) VALUES "
                    + "(NULL, '" + getJoueur() + "', '" + getAdminName() + "', '" + getUUID() + "', '" + getAdminUUID() + "' , '" + System.currentTimeMillis() + "', '" + getUntilTimestamp() + "', '" + getReason() + "', '', '', '1');");
        } catch (final SQLException ex) {
            System.out.println("SQL problem (exception) when add banned player to BDD : " + ex);
        }

    }

    public void removeFromBDD(String unbanReason, String unbanName) {
        try {
            if (Main.plugin.sql.getConnection().isClosed()) {
                Main.plugin.sql.open();
            }

            if (Main.plugin.sql.getConnection() == null) {
                System.out.println("[MYSQL] Connection error ...");
            }

            Main.plugin.sql.query("UPDATE `BungeeGuard_Ban` SET status='0', unbanReason='" + unbanReason + "', unbanName='" + unbanName + "' WHERE uuidBanned='" + getUUID() + "' AND status='1'");
            remove();
        } catch (final SQLException ex) {
            System.out.println("SQL problem (exception) when remove banned player to BDD : " + ex);
        }
    }

    public void remove() {
        Main.bans.remove(this);
    }
}
