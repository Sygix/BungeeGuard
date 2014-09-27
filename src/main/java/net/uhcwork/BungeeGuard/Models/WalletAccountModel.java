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

    public void setUUID(UUID u) {
        setUUID("" + u);
    }

    private void setUUID(String u) {
        setString("uuid", u);
    }

    public String getPlayerName() {
        return getString("player");
    }

    public void setPlayerName(String playerName) {
        setString("player", playerName);
    }

    public Integer getMoney() {
        return getInteger("money");
    }

    public void setMoney(int money) {
        setInteger("money", money);
    }

    public boolean isActive() {
        return getBoolean("active");
    }

    public void setActive(boolean active) {
        setBoolean("active", active);
    }
}
