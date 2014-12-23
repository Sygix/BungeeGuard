package net.uhcwork.BungeeGuard.Models;

import org.javalite.activejdbc.Model;
import org.javalite.activejdbc.annotations.Table;

import java.util.UUID;

@Table("bungee_friendvip")
public class BungeeFriendVIP extends Model {
    public void setSender(UUID sender) {
        setString("sender", "" + sender);
    }

    public void setRecipient(UUID recipient) {
        setString("recipient", "" + recipient);
    }
}
