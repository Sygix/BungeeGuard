package fr.greenns.BungeeGuard.Party;

import fr.greenns.BungeeGuard.Main;
import fr.greenns.BungeeGuard.MultiBungee.PubSub.PubSubBase;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
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
        ServerInfo SI = ProxyServer.getInstance().getServerInfo(serverName);
        Party p = plugin.getPM().getParty(partyName);
        if (SI == null)
            return;
        if (p == null)
            return;
        ProxiedPlayer pp;
        for (UUID uuid : p.getMembers()) {
            pp = ProxyServer.getInstance().getPlayer(uuid);
            if (pp == null)
                continue;
            if (p.isOwner(pp))
                continue;
            summon(pp, SI);
        }
    }

    private void summon(ProxiedPlayer player, ServerInfo target) {
        if (player.getServer() != null
                && !player.getServer().getInfo().equals(target)
                && player.getServer().getInfo().getName().startsWith("lobby")) {
            player.connect(target);
            player.sendMessage(new TextComponent(ChatColor.GOLD + "Envoi sur le serveur " + target.getName() + " ..."));
        }
    }
}
