package net.uhcwork.BungeeGuard.Models;

import org.javalite.activejdbc.Model;
import org.javalite.activejdbc.annotations.Table;

@Table("bungee_forced_host")
public class BungeeForcedHost extends Model {

    public String getIp() {
        return getString("ip");
    }

    public String getServer() {
        return getString("to_server");
    }
}
