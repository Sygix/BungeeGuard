package fr.PunKeel.BungeeGuard.Models;

import org.javalite.activejdbc.Model;
import org.javalite.activejdbc.annotations.IdName;
import org.javalite.activejdbc.annotations.Table;

import java.util.UUID;

@Table("Wallet_Accounts")
@IdName("uuid")
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

    public Double getMoney() {
        return getDouble("money");
    }

    public void setMoney(double money) {
        setDouble("money", money);
    }
}
