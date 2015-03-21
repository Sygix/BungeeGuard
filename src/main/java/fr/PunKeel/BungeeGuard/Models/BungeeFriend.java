package fr.PunKeel.BungeeGuard.Models;

import fr.PunKeel.BungeeGuard.Utils.UUIDUtils;
import org.javalite.activejdbc.Model;
import org.javalite.activejdbc.annotations.Table;

import java.sql.Timestamp;
import java.util.UUID;

@Table("bungee_friends")
public class BungeeFriend extends Model {

    public static BungeeFriend find(UUID userA, UUID userB) {
        return findFirst("uuid1=? AND uuid2=?", UUIDUtils.toBytes(userA), UUIDUtils.toBytes(userB));
    }

    public UUID getSender() {
        return UUIDUtils.fromBytes(getBytes("uuid1"));
    }

    public void setSender(UUID uuid) {
        set("uuid1", UUIDUtils.toBytes(uuid));
    }

    public UUID getReceiver() {
        return UUIDUtils.fromBytes(getBytes("uuid2"));
    }

    public void setReceiver(UUID uuid) {
        set("uuid2", UUIDUtils.toBytes(uuid));
    }

    public Timestamp getCreationDate() {
        return getTimestamp("created_at");
    }
}
