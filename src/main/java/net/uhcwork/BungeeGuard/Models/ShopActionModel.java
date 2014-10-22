package net.uhcwork.BungeeGuard.Models;

import org.javalite.activejdbc.Model;
import org.javalite.activejdbc.annotations.Table;

/**
 * Part of net.uhcwork.BungeeGuard.Models (BungeeGuard)
 * Date: 21/10/2014
 * Time: 20:07
 * May be open-source & be sold (by mguerreiro, of course !)
 */
@Table("bungee_shop")
public class ShopActionModel extends Model {
    public String getAction() {
        return getString("action");
    }
}
