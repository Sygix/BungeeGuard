package net.uhcwork.BungeeGuard.Models;

import org.javalite.activejdbc.Model;
import org.javalite.activejdbc.annotations.Table;

@Table("bungee_cmd")
public class BungeeCommandModel extends Model {
    public String getAction() {
        return getString("action");
    }
}
