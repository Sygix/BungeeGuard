package fr.greenns.BungeeGuard;

import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ServerPing;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.event.LoginEvent;
import net.md_5.bungee.api.event.ProxyPingEvent;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.event.ServerKickEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import fr.greenns.BungeeGuard.Lobbies.Lobby;

public class BungeeGuardListener implements Listener {

	public BungeeGuard plugin;

	public BungeeGuardListener(BungeeGuard plugin)
	{
		this.plugin = plugin;
	}


	@EventHandler
	public void onLogin(LoginEvent event)
	{
		Lobby l = plugin.lobbyUtils.bestLobbyTarget();
		if (l != null)
		{
			return;
		}

		event.setCancelled(true);
		event.setCancelReason(ChatColor.RED + "Nos services sont momentanément indisponibles"+'\n'+ChatColor.RED+"Veuillez réessayer dans quelques instants");
	}

	@EventHandler
	public void onServerConnect(ServerConnectEvent event)
	{
		if (event.getTarget().getName().equalsIgnoreCase("hub"))
		{
			Lobby l = plugin.lobbyUtils.bestLobbyTarget();

			if(l != null)
			{
				event.setTarget(l.getServerInfo());
			}
			else
			{
				if(BungeeCord.getInstance().getServerInfo("limbo").getPlayers().size()<70)
				{
					event.setTarget(BungeeCord.getInstance().getServerInfo("limbo"));
				}
				event.getPlayer().disconnect(ChatColor.RED + "Nos services sont momentanément indisponibles"+'\n'+ChatColor.RED+"Veuillez réessayer dans quelques instants");
			}
		}
	}


	@EventHandler
	public void onChat(ChatEvent event)
	{

		ProxiedPlayer p = (ProxiedPlayer) event.getSender();

		if (!event.getMessage().startsWith("/") && (event.getSender() instanceof ProxiedPlayer))
		{
			if(event.isCommand())
			{
				return;
			}
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
				if(p.hasPermission("bungeeguard.bypasschat"))
				{
					return;
				}
				event.setCancelled(true);
				p.sendMessage("§cLe chat est désactivé temporairement !");
			}
			if ((p.hasPermission("bungeeguard.staffchat")) && (event.getMessage().startsWith("!")))
			{
				for (ProxiedPlayer player : BungeeCord.getInstance().getPlayers())
				{
					if (player.hasPermission("bungeeguard.staffchat"))
					{
						player.sendMessage(new TextComponent(ChatColor.RED + "["+p.getServer().getInfo().getName()+"] "+p.getName()+": "+event.getMessage()));
					}
				}
				event.setCancelled(true);
			}
		}
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

	public ServerInfo getBestTarget(final ProxiedPlayer player) {
		return getBestTarget(player, null);
	}


	public ServerInfo getBestTarget(final ProxiedPlayer player, ServerInfo si)
	{
		ServerInfo best = null;
		List<ServerInfo> servers = new ArrayList<ServerInfo>();
		for (ServerInfo serverInfo : plugin.getProxy().getServers().values())
		{
			servers.add(serverInfo);
		}
		Collections.reverse(servers);
		for (final ServerInfo serverInfo : servers)
		{
			if (best == null && serverInfo.canAccess(player) && (plugin.serversUp.containsKey(serverInfo.getName()) && plugin.serversUp.get(serverInfo.getName()))) {
				best = serverInfo;
				continue;
			}
			if ((best != null && best.getPlayers().size() > serverInfo.getPlayers().size()) && plugin.serversUp.get(serverInfo.getName()) && serverInfo.canAccess(player) && (si == null ||si != serverInfo )) {
				best = serverInfo;
			}
		}
		return best;
	}


	@EventHandler
	public void onServerTurnOff(final ServerKickEvent event)
	{
		if(!(event.getKickReason().contains("ban") || event.getKickReason().contains("plein") || 
				event.getKickReason().contains("Full") || event.getKickReason().contains("fly") ||
				event.getKickReason().contains("Nos services") || event.getKickReason().contains("kické") ||
				event.getKickReason().contains("bannis") || event.getKickReason().contains("maintenance") ||
				event.getKickReason().contains("kick") || event.getKickReason().contains("VIP"))){
			ServerInfo best = getBestTarget(event.getPlayer(), event.getCancelServer());
			BungeeCord.getInstance().getConsole().sendMessage(new TextComponent(ChatColor.RED + "[BungeeGuard] " + event.getPlayer().getName() + " a perdu la connection (" + event.getState().toString() + " - " + event.getKickReason() + ")"));
			BungeeCord.getInstance().getConsole().sendMessage(new TextComponent(ChatColor.RED + "[BungeeGuard] " + event.getPlayer().getName() + " Redirigé vers "+ best.getName().toUpperCase()));
			event.getPlayer().setReconnectServer(best);
			event.setCancelled(true);
			event.setCancelServer(best);
			try {
				Thread.sleep(5);
			} catch (InterruptedException e) {
			}
			event.getPlayer().sendMessage("§cLe serveur sur lequel vous êtiez est probablement down vous avez été redirigé vers un serveur de secours ...");
		}
		else
		{
			event.getPlayer().disconnect(event.getKickReason());
			return;
		}
	}
}
