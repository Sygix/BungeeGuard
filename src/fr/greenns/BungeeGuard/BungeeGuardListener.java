package fr.greenns.BungeeGuard;

import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ServerPing;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.event.ProxyPingEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class BungeeGuardListener implements Listener {

    public BungeeGuard plugin;

    public BungeeGuardListener(BungeeGuard plugin)
    {
        this.plugin = plugin;
    }


    @EventHandler
    public void onChat(ChatEvent event)
    {
        ProxiedPlayer p = (ProxiedPlayer) event.getSender();

        if (plugin.mute.containsKey(p.getUUID().toString()))
        {
            long time = plugin.mute.get(p.getUUID().toString());

            long unixTime = System.currentTimeMillis() / 1000L;
            if(unixTime-time > 0)
            {
                plugin.mute.remove(p.getUUID().toString());
                p.sendMessage("§7Vous avez été §adémuté §7!");
            }
            else
            {
                event.setCancelled(true);
                p.sendMessage("§cVous êtes muté temporairement !");
            }
        }
        if(plugin.serv.contains(p.getServer().getInfo().getName()))
        {
            if(event.isCommand())
            {
                return;
            }
            if(p.hasPermission("bungeeguard.bypasschat"))
            {
                return;
            }
            event.setCancelled(true);
            p.sendMessage("§cLe chat est désactivé temporairement !");
        }
    }

    @EventHandler
    public void onPing(ProxyPingEvent e)
    {
    }

    @EventHandler
    public void onProxyPing(ProxyPingEvent e)
    {
        ServerPing sp = e.getResponse();
        sp.setDescription(plugin.motd);

        List<String> lines = new ArrayList();
        /*lines.add("§M§L                 §r§l«§6§l UHC §b§lNetwork §r§l»§M§L                 ");
        lines.add("§7 ");
        lines.add("§7§oUHCGames est un serveur de jeux UltraHardCore.");
        lines.add("§7§o   Vous aimez le stress, la difficulté ?");
        lines.add("§7§o       UHCGames est fait pour vous !");
        lines.add("§7 ");
        lines.add("§4➟ §cKill The Patrick §7- §4Joués comme les Patricks à KTP !");
        lines.add("§6➟ §eUltra HungerGames §7- §6Un HungerGames en UltraHardCore !");
        lines.add("§1➟ §9Rush §7- §1Le meilleur PvP Bed Wars !");
        lines.add("§3➟ §bFatality §7- §3Détruisez les deux coeurs et vous serez le meilleur !");
        lines.add("§5➟ §dTower §7- §5Défendez votre base tout en marquant dans celle des adversaires !");
        lines.add("§2➟ §aFightOnFaces §7- §2Battez vous sur une arène de joueurs !");
        lines.add("§7          Et bien d'autres jeux ...");*/

        lines.add("§M§L         §r§l«§6§l UHC §b§lNetwork §r§l»§M§L         ");
        lines.add("§7 ");
        lines.add("§7§oUn serveur de jeux UltraHardCore !");
        lines.add("§7§o  Stress, Difficulté, Travail d'équipe");
        lines.add("§7§o      Vous allez aimer UHCGames !");
        lines.add("§7 ");
        lines.add("§7➟ §cKill The Patrick");
        lines.add("§7➟ §eUltra HungerGames");
        lines.add("§7➟ §9Rush");
        lines.add("§7➟ §bFatality");
        lines.add("§7➟ §dTower");
        lines.add("§7➟ §aFightOnFaces");
        lines.add("§7Et bien d'autres jeux ...");

        ServerPing.PlayerInfo[] players = new ServerPing.PlayerInfo[lines.size()];
        for (int i = 0; i < players.length; i++)
        {
            players[i] = new ServerPing.PlayerInfo((String)lines.get(i), "");
        }
        e.getResponse().getPlayers().setSample(players);
    }
}
