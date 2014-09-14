package fr.greenns.BungeeGuard.commands;

/**
 * Part of ${PACKAGE_NAME} (${PROJECT_NAME})
 * Date: 07/09/2014
 * Time: 18:45
 * May be open-source & be sold (by mguerreiro, of course !)
 */

import com.imaginarycode.minecraft.redisbungee.RedisBungee;
import com.imaginarycode.minecraft.redisbungee.internal.jedis.Jedis;
import com.imaginarycode.minecraft.redisbungee.internal.jedis.JedisPool;
import com.imaginarycode.minecraft.redisbungee.internal.jedis.exceptions.JedisConnectionException;
import fr.greenns.BungeeGuard.Main;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;

public class CommandBLoad extends Command {
    public Main plugin;

    public CommandBLoad(Main plugin) {
        super("b:load", "bungeeguard.bload");
        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        RedisBungee x = (RedisBungee) BungeeCord.getInstance().getPluginManager().getPlugin("RedisBungee");
        JedisPool pool = x.getPool();
        long c;
        if (pool != null) {
            Jedis rsc = pool.getResource();
            try {
                for (String i : plugin.getMB().getAllServers()) {
                    c = rsc.scard("proxy:" + i + ":usersOnline");
                    sender.sendMessage(i + ": " + c + " joueur(s)");
                }
            } catch (JedisConnectionException e) {
                // Redis server has disappeared!
                pool.returnBrokenResource(rsc);
                throw new RuntimeException("Unable to get total player count", e);
            } finally {
                pool.returnResource(rsc);
            }
        }
    }
}