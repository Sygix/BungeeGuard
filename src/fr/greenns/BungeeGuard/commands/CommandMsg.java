package fr.greenns.BungeeGuard.commands;

import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import fr.greenns.BungeeGuard.BungeeGuard;

import java.util.UUID;

public class CommandMsg extends Command {

    public BungeeGuard plugin;

    public CommandMsg(BungeeGuard plugin)
    {
        super("msg", "bungeeguard.msg", "m", "w", "tell", "whisper");
        this.plugin = plugin;
    }

    public void execute(CommandSender sender, String[] args)
    {
        if(sender instanceof ProxiedPlayer)
        {
            ProxiedPlayer p = (ProxiedPlayer)sender;

            if(!p.hasPermission("bungeeguard.msg"))
            {
                return;
            }
        }
        else
        {
            sender.sendMessage(ChatColor.RED + "Vous devez etre un joueur pour executer cette command !");
            return;
        }

        ProxiedPlayer p = (ProxiedPlayer)sender;

        if(plugin.mute.containsKey(plugin.mute.containsKey(p.getName())))
        {
            p.sendMessage("§cVous êtes muté temporairement !");
            return;
        }

        if(args.length == 0)
        {
            p.sendMessage("§cExemple :");
            p.sendMessage("§c/msg NomDeMonAmi Hey sa te dit de jouer avec moi ?");
            return;
        }

        if(args.length >= 1 )
        {
            if (!args[0].equalsIgnoreCase(p.getName()))
            {
                for (ProxiedPlayer pl : BungeeCord.getInstance().getPlayers())
                {
                    if (args[0].equalsIgnoreCase(pl.getName()))
                    {
                        String text1 = "";
                        for (int i = 1; i < args.length; i++)
                            text1 = text1 + args[i] + " ";

                        String text = text1;
                        pl.sendMessage("§8[§a" + p.getName() + " §7➠  §aMoi§8] §f" + text);
                        p.sendMessage("§8[§aMoi §7➠  §a" + pl.getName() + "§8] §f" + text);
                        plugin.reply.put(p.getName(), pl);
                        plugin.reply.put(pl.getName(), (ProxiedPlayer)p);
                        for(String sp : plugin.spy)
                        {
                            try
                            {
                                ProxiedPlayer admin = BungeeCord.getInstance().getPlayer(UUID.fromString(sp));
                                admin.sendMessage("§7[§cSPY§7] "+ChatColor.GRAY + p.getName() + ": /msg " + pl.getName() + " " + text);
                            }
                            catch (Exception e)
                            {

                            }
                        }

                        return;
                    }
                }
                p.sendMessage("§cLe joueur que vous chercher a contacter n'est pas en ligne !");
            }
            else
            {
                p.sendMessage("§cVous ne pouvez pas envoyer un message à vous-même !");
            }
        }
    }
}
