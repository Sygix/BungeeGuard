package fr.PunKeel.BungeeGuard.Managers;

import fr.PunKeel.BungeeGuard.Main;
import fr.PunKeel.BungeeGuard.Models.BungeeCheat;
import fr.PunKeel.BungeeGuard.Persistence.SaveRunner;

public class CheatManager {
    private final Main plugin;

    public CheatManager(Main plugin) {
        this.plugin = plugin;

    }

    public void addCheat(String serverName, String playerName, String cheatName, double score) {
        BungeeCheat BC = new BungeeCheat();
        BC.setPlayerName(playerName);
        BC.setServerName(serverName);
        BC.setCheatType(cheatName);
        BC.setCheatScore(score);
        plugin.executePersistenceRunnable(new SaveRunner(BC));
    }
}
