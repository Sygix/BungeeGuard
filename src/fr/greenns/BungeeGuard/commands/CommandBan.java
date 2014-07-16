package fr.greenns.BungeeGuard.commands;

import java.net.InetAddress;
import java.sql.ResultSet;
import java.sql.SQLException;

import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import fr.greenns.BungeeGuard.BungeeGuard;

public class CommandBan extends Command {

    public BungeeGuard plugin;

    public CommandBan(BungeeGuard plugin)
    {
        super("ban", "bungeeguard.ban");
        this.plugin = plugin;
    }

    public void execute(CommandSender sender, String[] args)
    {
        String name;
        String uuidA;
        if(sender instanceof ProxiedPlayer)
        {
            ProxiedPlayer p = (ProxiedPlayer)sender;

            if(!p.hasPermission("bungeeguard.ban"))
            {
                return;
            }

            name = p.getName();
            uuidA = p.getUUID().toString();
        }
        else
        {
            name = "*Console*";
            uuidA = "";
        }

        if(args.length == 0)
        {
            plugin.utils.msgPluginCommand(sender);
            return;
        }

        String msg = "";
        String kickmsg = "";
        String powodd = "";
        String time = "0"; // normal non unix time
        String days ="";
        String hours ="";
        String minutes ="";
        String nick = "";
        String uuidB = "";
        int minuteIncrease = 0;
        int hourIncrease = 0;
        int dateIncrease = 0;
        int czas = 0; // ban time in in unix settings
        int initialMinute;
        boolean onlinePlayer = false;
        boolean noTime = false;

        if(args.length > 0)
        {
            if(BungeeCord.getInstance().getPlayer(args[0]) != null)
            {
                ProxiedPlayer cel = BungeeCord.getInstance().getPlayer(args[0]);
                nick = cel.getName();
                uuidB = cel.getUUID().toString();
                onlinePlayer = true;
            }
            else
            {
                nick = args[0];
                try
                {
                    uuidB = BungeeCord.getInstance().getPlayer(args[0]).getUUID().toString();
                }
                catch (Exception e){}
                onlinePlayer = false;
                if(nick.isEmpty())
                {
                    sender.sendMessage("§cNom du joueur incorrecte ...");
                    return;
                }
            }

            if(args.length == 1)
            {
                kickmsg = "§cVous etes banni définitivement par " + name +" !";
                msg = "§c" + name + " a bannis définitivement " + nick + " !";
            }
        }
        if(args.length > 1)
        {

            long dura = plugin.utils.parseDuration(args[1]);

            if(args[0] != null)
            {
                if(plugin.utils.isParsableToInt(args[1]))
                {
                    if(Integer.valueOf(args[1])<=0)
                    {
                        time = args[1] = "0";
                    }
                    else
                    {
                        long unixTime = System.currentTimeMillis() / 1000L;
                        czas = (int)(unixTime+dura);
                        time = args[1]=args[1];
                    }
                }
                else
                {
                    time = args[1] = "0";
                }
            }
            else
            {
                time = args[1] = "0";
            }

                kickmsg = "§cVous etes bannis pour " + plugin.utils.getDuration(dura) + " par " + name +" !";
                msg = "§c" + name + " a bannis " + nick + " " + plugin.utils.getDuration(dura) + " !";
        }
        if(args.length > 2)
        {
            long dura = plugin.utils.parseDuration(args[1]);
            powodd = "";
            if ( noTime )
                for( int a=1; a<args.length;a++)powodd += " "+args[a];
            else
                for( int a=2; a<args.length;a++)powodd += " "+args[a];

            if(noTime)
            {
                kickmsg = "§cVous etes banni définitivement par " + name +" pour: \n" + powodd + " !";
                msg = "§c" + name + " a bannis définitivement " + nick + " pour:" + powodd + " !";
            }
            else
            {
                kickmsg = "§cVous etes bannis pour " +plugin.utils.getDuration(dura)+ " par " + name+" pour: \n" + powodd + " !";
                msg = "§c" + name + " a bannis " + nick + " " +plugin.utils.getDuration(dura) + " pour:" + powodd + " !";
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


        String safenick = nick.toLowerCase().replaceAll("'", "\"");
        String safeAdminNick = name.replaceAll("'", "\"");
        String safereason = powodd.replaceAll("'", "\"");

        plugin.sql.open();

        if(plugin.sql.getConnection() == null)
        {
            sender.sendMessage(ChatColor.RED+"[MYSQL] Connection error ...");
            return;
        }
        ProxiedPlayer cel = BungeeCord.getInstance().getPlayer(args[0]);
        int banfrom = (int) (System.currentTimeMillis() / 1000L);
        InetAddress ipcel = cel.getAddress().getAddress();
        String ipst = ipcel.toString();
        String ip = ipst.replaceAll("/", "");

        if(safereason == null)
        {
            safereason="";
        }

        if(onlinePlayer==true)
        {
            cel.disconnect(new TextComponent(kickmsg));
        }

        try
        {
            if(onlinePlayer)
            {
                plugin.sql.query("INSERT INTO `BungeeGuard_Ban` (`id`, `nameBanned`, `nameAdmin` , `uuidBanned` , `uuidAdmin` , `ip`, `ban`, `unban`, `reason`, `unbanReason`, `unbanName`, `status`) VALUES (NULL, '"+safenick+"', '" + safeAdminNick + "', '" + uuidA + "', '" + uuidB + "' ,'"+ip+"', '"+banfrom+"', '"+czas+"', '"+safereason+"', '', '', '1');");
            }
            else
            {
                plugin.sql.query("INSERT INTO `BungeeGuard_Ban` (`id`, `nameBanned`, `nameAdmin` , `ip`, `ban`, `unban`, `reason`, `unbanReason`, `unbanName`, `status`) VALUES (NULL, '"+safenick+"', '" + safeAdminNick + "', '" + uuidA + "', '" + uuidB + "','', '"+banfrom+"', '"+czas+"', '"+safereason+"', '', '', '1');");
            }
            if (!plugin.sql.getConnection().isClosed())
            {
                plugin.sql.close();
            }
        }
        catch (SQLException ex)
        {
            System.out.println(ex);
        }
    }
}
