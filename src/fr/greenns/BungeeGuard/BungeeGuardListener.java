package fr.greenns.BungeeGuard;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

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

		if (plugin.mute.containsKey(p.getName()))
		{
			long time = plugin.mute.get(p.getName());

			long unixTime = System.currentTimeMillis() / 1000L;
			if(unixTime-time > 0)
			{
				plugin.mute.remove(p.getName());
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
}
