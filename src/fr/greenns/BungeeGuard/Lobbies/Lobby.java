package fr.greenns.BungeeGuard.Lobbies;

import java.util.concurrent.TimeUnit;

import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.config.ServerInfo;
import fr.greenns.BungeeGuard.BungeeGuard;

public class Lobby {

	public BungeeGuard plugin;

	public Lobby(BungeeGuard plugin)
	{
		this.plugin = plugin;
	}

	private String name;
	private int slot;

	public Lobby(String name, int slot, BungeeGuard pl)
	{
		this.name = name;
		this.slot = slot;
		updateSlot();
		pl.lobbyList.add(this);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getSlot() {
		return slot;
	}

	public void setSlot(Integer slot) {
		this.slot = slot;
	}

	public ServerInfo getServerInfo()
	{
		return BungeeCord.getInstance().getServerInfo(this.name);
	}

	public void updateSlot()
	{
		BungeeCord.getInstance().getScheduler().schedule(plugin, new Runnable() {
            @SuppressWarnings("deprecation")
            @Override
            public void run()
            {
        		int newSlot = getServerInfo().getPlayers().size();
        		setSlot(slot);
            }
        }, 5, 5, TimeUnit.SECONDS);
	}
}
