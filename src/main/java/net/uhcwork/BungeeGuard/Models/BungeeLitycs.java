package net.uhcwork.BungeeGuard.Models;

import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import org.javalite.activejdbc.Model;
import org.javalite.activejdbc.annotations.Table;

import java.sql.Timestamp;
import java.util.UUID;

/**
 * Part of net.uhcwork.BungeeGuard.Models (BungeeGuard)
 * Date: 02/11/2014
 * Time: 03:32
 * May be open-source & be sold (by mguerreiro, of course !)
 */
@Table("bungeelitycs")
public class BungeeLitycs extends Model {
    public void setUUID(UUID uniqueId) {
        setString("uuid", "" + uniqueId);
    }

    public void setPlayerName(String name) {
        setString("player_name", name);
    }

    public void setServerID(String name) {
        setString("server_id", name);
    }

    public void setJoinedAt(Timestamp joinedAt) {
        setTimestamp("joined_at", joinedAt);
    }

    public void setLeavedAt(Timestamp leavedAt) {
        setTimestamp("leaved_at", leavedAt);
    }

    public int getID() {
        return (int) getId();
    }

    public void join(ProxiedPlayer p, ServerInfo target) {
        setUUID(p.getUniqueId());
        setPlayerName(p.getName());
        setServerID(target.getName());
        setJoinedAt(new Timestamp(System.currentTimeMillis()));
    }

    public void leave(ProxiedPlayer p) {
        setLeavedAt(new Timestamp(System.currentTimeMillis()));
    }
}
