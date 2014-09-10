package fr.greenns.BungeeGuard.Party;

import fr.greenns.BungeeGuard.Main;
import fr.greenns.BungeeGuard.PubSub.PubSubBase;
import fr.greenns.BungeeGuard.utils.ComponentManager;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.UUID;

/**
 * Part of fr.greenns.BungeeGuard (bungeeguard)
 * Date: 10/09/2014
 * Time: 21:55
 * May be open-source & be sold (by mguerreiro, of course !)
 */
public class PartySummonHandler extends PubSubBase {
    private final Main plugin;

    public PartySummonHandler(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public void handle(String channel, String message, String[] args) {
        String partyName = args[0];
        String serverName = args[1];
        ServerInfo SI = BungeeCord.getInstance().getServerInfo(serverName);
        Party p = plugin.getPM().getParty(partyName);
        if (SI == null)
            return;
        if (p == null)
            return;
        ProxiedPlayer pp;
        for (UUID uuid : p.getMembers()) {
            pp = BungeeCord.getInstance().getPlayer(uuid);
            if (pp == null)
                continue;
            summon(pp, SI);
        }
    }

    private void summon(ProxiedPlayer player, ServerInfo target) {
        if (player.getServer() != null && !player.getServer().getInfo().equals(target)) {
            player.connect(target);
            player.sendMessage(ComponentManager.generate(ChatColor.GOLD + "Envoi sur le serveur " + target.getName() + " ..."));
        }
    }
}
