package net.uhcwork.BungeeGuard.Models;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
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
    public static byte[] toBytes(UUID uuid) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput(16);
        out.writeLong(uuid.getMostSignificantBits());
        out.writeLong(uuid.getLeastSignificantBits());
        return out.toByteArray();
    }

    public void setUUID(UUID u) {
        set("uuid", toBytes(u));
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

    public void join(ProxiedPlayer p, ServerInfo target) {
        setUUID(p.getUniqueId());
        setServerID(target.getName());
        setJoinedAt(new Timestamp(System.currentTimeMillis()));
    }

    public void leave(ProxiedPlayer p) {
        setLeavedAt(new Timestamp(System.currentTimeMillis()));
    }
}
