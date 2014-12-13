package net.uhcwork.BungeeGuard.Models;

import org.javalite.activejdbc.Model;
import org.javalite.activejdbc.annotations.Table;

@Table("bungee_shop")
public class ShopActionModel extends Model {
    public String getAction() {
        return getString("action");
    }
}
