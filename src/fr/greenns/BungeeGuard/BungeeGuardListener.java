package fr.greenns.BungeeGuard;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ServerPing;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.event.LoginEvent;
import net.md_5.bungee.api.event.ProxyPingEvent;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.event.ServerConnectedEvent;
import net.md_5.bungee.api.event.ServerKickEvent;
import net.md_5.bungee.api.event.TabCompleteEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import fr.greenns.BungeeGuard.Ban.Ban;
import fr.greenns.BungeeGuard.Ban.BanType;
import fr.greenns.BungeeGuard.Lobbies.Lobby;
import fr.greenns.BungeeGuard.Mute.Mute;
import fr.greenns.BungeeGuard.Mute.MuteType;
import fr.greenns.BungeeGuard.utils.AuthPlayer;
import fr.greens.BungeeGuard.Authenticator.Authenticator;

public class BungeeGuardListener implements Listener {

	public BungeeGuard plugin;

	public BungeeGuardListener(BungeeGuard plugin)
	{
		this.plugin = plugin;
	}
	
	@EventHandler
	public void onLogin(LoginEvent event)
	{
		Ban BannedUser = BungeeGuardUtils.getBan(event.getConnection().getUniqueId());
		if(BannedUser != null) {
			if(BannedUser.isDefBanned()) {
				event.setCancelled(true);
				
				BanType BanType = (BannedUser.getReason() != null) ? fr.greenns.BungeeGuard.Ban.BanType.PERMANENT_W_REASON : fr.greenns.BungeeGuard.Ban.BanType.PERMANENT;
				String CancelMsg = BanType.kickFormat("", BannedUser.getReason());
				
				event.setCancelReason(CancelMsg);
				return;
			}
			else if(BannedUser.isBanned()) {
				event.setCancelled(true);
				
				String durationStr = BungeeGuardUtils.getDuration(BannedUser.getTime());
				BanType BanType = (BannedUser.getReason() != null) ? fr.greenns.BungeeGuard.Ban.BanType.NON_PERMANENT_W_REASON : fr.greenns.BungeeGuard.Ban.BanType.NON_PERMANENT;
				String CancelMsg = BanType.kickFormat(durationStr, BannedUser.getReason());
				
				event.setCancelReason(CancelMsg);
				return;
			}
			else {
				BannedUser.removeFromBDD("TimeEnd", "Automatique");
			}
		}
		
		Lobby l = plugin.lobbyUtils.bestLobbyTarget();
		if (l != null)
		{
			return;
		}

		event.setCancelled(true);
		event.setCancelReason(ChatColor.RED + "Nos services sont momentanément indisponibles"+'\n'+ChatColor.RED+"Veuillez réessayer dans quelques instants");
	}
	
	@EventHandler
	public void onServerConnect(ServerConnectEvent e)
	{
		AuthPlayer AuthPlayer = BungeeGuardUtils.getUnloggedAuthPlayer(e.getPlayer().getUniqueId());
		if(AuthPlayer != null){
			e.setTarget(BungeeCord.getInstance().getServerInfo("loginserver"));
			return;
		}
		
		if (e.getTarget().getName().equalsIgnoreCase("hub"))
		{
			Lobby l = plugin.lobbyUtils.bestLobbyTarget();

			if(l != null)
			{
				e.setTarget(l.getServerInfo());
			}
			else
			{
				if(BungeeCord.getInstance().getServerInfo("limbo").getPlayers().size()<70)
				{
					e.setTarget(BungeeCord.getInstance().getServerInfo("limbo"));
				}
				e.getPlayer().disconnect(new ComponentBuilder(ChatColor.RED + "Nos services sont momentanément indisponibles"+'\n'+ChatColor.RED+"Veuillez réessayer dans quelques instants").create());
			}
		}
	}

	@EventHandler
	public void onServerConnected(ServerConnectedEvent event)
	{
		if (event.getServer().getInfo().getName().equalsIgnoreCase("loginserver")) {
			event.getPlayer().sendMessage(new ComponentBuilder("Bienvenue sur UHCGames !").color(ChatColor.AQUA).create());
			event.getPlayer().sendMessage(new ComponentBuilder("Veuillez saisir votre code d'authentification dans le chat pour vous connecter.").color(ChatColor.RED).create());
		}
	}

	@EventHandler
	public void onChat(ChatEvent e)
	{
		ProxiedPlayer p = (ProxiedPlayer) e.getSender();
		AuthPlayer AuthPlayer = BungeeGuardUtils.getUnloggedAuthPlayer(p.getUniqueId());
		if(AuthPlayer != null){
			if(e.isCommand()) {
				p.sendMessage(new ComponentBuilder("Veuillez saisir votre code d'authentification dans le chat pour vous connecter.").color(ChatColor.RED).create());
			} else {
				String code = e.getMessage();
				String secretKey = AuthPlayer.getSecretKey();
				if(Authenticator.valid_code(code, secretKey)) {
					p.sendMessage(new ComponentBuilder("Code validé ! Vous êtes maintenant connecté !").color(ChatColor.GREEN).create());
					AuthPlayer.setLogged();
					p.connect(BungeeCord.getInstance().getServerInfo("hub"));
				} else {
					p.disconnect(new ComponentBuilder("Code Invalide ! Vous avez été déconnecté !").color(ChatColor.RED).create());
				}
			}
			
			e.setCancelled(true);
			return;
		}

		if (!e.getMessage().startsWith("/") && (e.getSender() instanceof ProxiedPlayer)) {
			
			if(e.isCommand()) {
				return;
			}
			Mute MuteUser = BungeeGuardUtils.getMute(p.getUniqueId());
			if (MuteUser != null) {

				if(MuteUser.isMute()) {
					MuteType MuteType = (MuteUser.getReason() != null) ? fr.greenns.BungeeGuard.Mute.MuteType.NON_PERMANENT_W_REASON : fr.greenns.BungeeGuard.Mute.MuteType.NON_PERMANENT;
					String MuteMsg = MuteType.playerFormat("", MuteUser.getReason());
					p.sendMessage(new ComponentBuilder(MuteMsg).create());		
				}
				else {
					MuteUser.removeFromBDD("TimeEnd", "Automatique");
				}
			}
			if(plugin.serv.contains(p.getServer().getInfo().getName()))
			{
				if(p.hasPermission("bungeeguard.bypasschat"))
				{
					return;
				}
				e.setCancelled(true);
				p.sendMessage(new ComponentBuilder("Le chat est désactivé temporairement !").color(ChatColor.RED).create());
			}
			if ((p.hasPermission("bungeeguard.staffchat")) && (e.getMessage().startsWith("!")))
			{
				for (ProxiedPlayer player : BungeeCord.getInstance().getPlayers())
				{
					if (player.hasPermission("bungeeguard.staffchat"))
					{
						player.sendMessage(new TextComponent(ChatColor.RED + "["+p.getServer().getInfo().getName()+"] "+p.getName()+": "+e.getMessage()));
					}
				}
				e.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void onProxyPing(ProxyPingEvent e)
	{
		ServerPing sp = e.getResponse();
		sp.setDescription(plugin.motd);

		List<String> lines = new ArrayList<String>();

		lines.add(ChatColor.STRIKETHROUGH + "" + ChatColor.BOLD + "         " + ChatColor.RESET + "" + ChatColor.BOLD +"«" + ChatColor.GOLD + "" + ChatColor.BOLD + " UHC " + ChatColor.AQUA + "" + ChatColor.BOLD + "Network " + ChatColor.RESET + "" + ChatColor.BOLD + "»" +ChatColor.STRIKETHROUGH + "" + ChatColor.BOLD + "         ");
		lines.add(ChatColor.GRAY + " ");
		lines.add(ChatColor.GRAY + "" + ChatColor.ITALIC + "Un serveur de jeux UltraHardCore !");
		lines.add(ChatColor.GRAY + "" + ChatColor.ITALIC + "  Stress, Difficulté, Travail d'équipe");
		lines.add(ChatColor.GRAY + "" + ChatColor.ITALIC + "      Vous allez aimer UHCGames !");
		lines.add(ChatColor.GRAY + " ");
		lines.add(ChatColor.GRAY + "➟ " + ChatColor.RED + "Kill The Patrick");
		lines.add(ChatColor.GRAY + "➟ " + ChatColor.YELLOW + "Ultra HungerGames");
		lines.add(ChatColor.GRAY + "➟" + ChatColor.BLUE + "Rush");
		lines.add(ChatColor.GRAY + "➟ " + ChatColor.AQUA + "Fatality");
		lines.add(ChatColor.GRAY + "➟ " + ChatColor.LIGHT_PURPLE + "Tower");
		lines.add(ChatColor.GRAY + "➟ " + ChatColor.GREEN + "FightOnFaces");
		lines.add(ChatColor.GRAY + "Et bien d'autres jeux ...");
		
		

		ServerPing.PlayerInfo[] players = new ServerPing.PlayerInfo[lines.size()];
		for (int i = 0; i < players.length; i++)
		{
			players[i] = new ServerPing.PlayerInfo(lines.get(i), "");
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


	@SuppressWarnings({ "deprecation"})
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
			event.getPlayer().sendMessage(new ComponentBuilder("Le serveur sur lequel vous êtiez est probablement down vous avez été redirigé vers un serveur de secours ...").color(ChatColor.RED).create());
		}
		else
		{
			event.getPlayer().disconnect(new ComponentBuilder(event.getKickReason()).create());
			return;
		}
	}
	
	@EventHandler
	public void onTabCompleteEvent(TabCompleteEvent e) {
		String[] Message = e.getCursor().split("\\s+");
		String DebutDePseudo = Message[Message.length-1].toLowerCase();
		for(ProxiedPlayer p: plugin.getProxy().getPlayers()) {
			if(p.getName().toLowerCase().startsWith(DebutDePseudo)) e.getSuggestions().add(p.getName());
		}
	}
}
