package net.uhcwork.BungeeGuard.Models;

import org.javalite.activejdbc.Model;
import org.javalite.activejdbc.annotations.Table;

import java.util.UUID;

/**
 * Part of net.uhcwork.BungeeGuard.Wallet (bungeeguard)
 * Date: 27/09/2014
 * Time: 21:44
 * May be open-source & be sold (by mguerreiro, of course !)
 */
@Table("Wallet_Accounts")
public class WalletAccountModel extends Model {
    public String getUUID() {
        return getString("uuid");
    }

    private void setUUID(String u) {
        setString("uuid", u);
    }

    public void setUUID(UUID u) {
        setUUID("" + u);
    }

    public String getPlayerName() {
        return getString("player");
    }

    public void setPlayerName(String playerName) {
        setString("player", playerName);
    }

    public Double getMoney() {
        return getDouble("money");
    }

    public void setMoney(double money) {
        setDouble("money", money);
    }

    public boolean isActive() {
        return getBoolean("active");
    }

    public void setActive(boolean active) {
        setBoolean("active", active);
    }
}
