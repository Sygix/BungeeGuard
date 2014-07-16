package fr.greenns.BungeeGuard.commands;

import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import fr.greenns.BungeeGuard.BungeeGuard;

import java.util.UUID;

public class CommandMute extends Command {

    public BungeeGuard plugin;
    public long czas;

    public CommandMute(BungeeGuard plugin)
    {
        super("mute", "bungeeguard.mute");
        this.plugin = plugin;
    }

    public void execute(CommandSender sender, String[] args)
    {
        String name;

        if(sender instanceof ProxiedPlayer)
        {
            ProxiedPlayer p = (ProxiedPlayer)sender;

            if(!p.hasPermission("bungeeguard.mute"))
            {
                return;
            }
            name = p.getDisplayName();
        }
        else
        {
            name = "*Console*";
        }

        if(args.length == 0)
        {
            plugin.utils.msgPluginCommand(sender);
            return;
        }

        String msg = "";
        String targetmsg = "";
        String powodd = "";
        String timeformat = "";
        String nick = "";
        czas = 0; // ban time in in unix settings
        long initialMinute;
        boolean onlinePlayer = false;
        boolean noTime = false;

        if(args.length > 0)
        {
            if(BungeeCord.getInstance().getPlayer(args[0]) != null)
            {
                ProxiedPlayer cel = BungeeCord.getInstance().getPlayer(args[0]);
                nick = cel.getName();
                onlinePlayer = true;
            }
            else
            {
                nick = args[0];
                onlinePlayer = false;
                if(nick.isEmpty())
                {
                    sender.sendMessage("§cNom du joueur incorrecte ...");
                    return;
                }
            }

            if(args.length == 1)
            {
                targetmsg = "§cVous etes muté 24 heures par " + name +" !";
                msg = "§c" + name + " a muté 24 heures " + nick + " !";

                long unixTime = System.currentTimeMillis() / 1000L;
                czas = (int)(unixTime+(1440*60));
            }
        }
        if(args.length > 1)
        {
            if(args[0] != null)
            {
                String temps = "";
                for( int a=1; a<args.length;a++)temps += " "+args[a];
                initialMinute = plugin.utils.parseDuration(temps);
                long unixTime = System.currentTimeMillis();
                czas = (unixTime+initialMinute);
                timeformat = plugin.utils.getDuration(initialMinute);
            }
            else
            {
                noTime = true;
                initialMinute = 86400000;
                long unixTime = System.currentTimeMillis();
                czas = (unixTime+initialMinute);
                timeformat = plugin.utils.getDuration(initialMinute);
            }

            targetmsg = "§cVous etes muté pour " + timeformat + "par " + name +" !";
            msg = "§c" + name + " a muté " + nick + " " + timeformat + "!";
        }
        else if(args.length > 2)
        {
            powodd = "";
            if ( noTime )
                for( int a=1; a<args.length;a++)powodd += " "+args[a];
            else
                for( int a=2; a<args.length;a++)powodd += " "+args[a];
        }


        if(onlinePlayer)
        {
            ProxiedPlayer cel = BungeeCord.getInstance().getPlayer(args[0]);

            if(plugin.mute.containsKey(cel.getUUID().toString()))
            {
                sender.sendMessage("§c"+cel.getName()+" est deja mute !");
                return;
            }
            else
            {
                plugin.mute.put(cel.getUUID().toString(),czas);
            }
            cel.sendMessage(targetmsg);
        }
        else
        {
            if(plugin.mute.containsKey(BungeeCord.getInstance().getPlayer(args[0]).getUUID().toString()))
            {
                sender.sendMessage("§c"+args[0]+" est deja mute !");
                return;
            }
            else
            {
                plugin.mute.put(BungeeCord.getInstance().getPlayer(args[0]).getUUID().toString(),czas);
            }
        }


        System.out.println(msg);

        for (ProxiedPlayer playerdwa : BungeeCord.getInstance().getPlayers())
        {
            if(playerdwa.hasPermission("bungeeguard.notify"))
            {
                playerdwa.sendMessage(plugin.utils.staffBroadcast + msg);
            }
        }
    }
}
