package net.uhcwork.BungeeGuard.Commands;


import com.google.common.io.Resources;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.uhcwork.BungeeGuard.Main;
import net.uhcwork.BungeeGuard.Utils.StringTemplate;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Part of net.uhcwork.BungeeGuard.Commands (BungeeGuard)
 * Date: 11/10/2014
 * Time: 16:42
 * May be open-source & be sold (by mguerreiro, of course !)
 */
public class CommandRegister extends Command {

    private final Main plugin;
    private final StringTemplate url;
    boolean active = true;

    public CommandRegister(Main plugin) {
        super("register", "");
        this.plugin = plugin;
        active = false;
        url = new StringTemplate("http://uhcgames.com/ajax/add_member_sv.php?pseudo=${pseudo}&uuid=${uuid}&email=${email}&v=${token}&ip=${ip}");
    }

    @Override
    public void execute(final CommandSender sender, String[] args) {
        if (args.length != 1) {
            sender.sendMessage(TextComponent.fromLegacyText(ChatColor.RED + "Usage: /register <email>"));
            return;
        }
        if (!(sender instanceof ProxiedPlayer)) {
            sender.sendMessage(TextComponent.fromLegacyText(ChatColor.RED + "Tu veux que je te " + ChatColor.YELLOW + "console " + ChatColor.RED + " ? >.<"));
            return;
        }
        if (!active) {
            sender.sendMessage(TextComponent.fromLegacyText(ChatColor.RED + "Bien trouvé ... Mais pas encore activé :("));
            return;
        }
        ProxiedPlayer p = (ProxiedPlayer) sender;
        String email = args[0];
        String emailPattern = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        if (!Pattern.matches(emailPattern, email)) {
            sender.sendMessage(TextComponent.fromLegacyText(ChatColor.RED + "Cette adresse mail semble invalide."));
            return;
        }
        final Map<String, String> valeurs = new HashMap<>();
        valeurs.put("token", "m82A6FZ56pd5tZmT");
        valeurs.put("pseudo", p.getName());
        valeurs.put("uuid", p.getUniqueId().toString().replace("-", ""));
        valeurs.put("email", args[0]);
        valeurs.put("ip", p.getAddress().getAddress().getHostAddress());

        plugin.getProxy().getScheduler().runAsync(plugin, new Runnable() {
            @Override
            public void run() {
                try {
                    URL link = new URL(url.substitute(valeurs));

                    String result = Resources.readLines(link, Charset.defaultCharset()).get(0).trim();
                    switch (result) {
                        case "1":
                            sender.sendMessage(TextComponent.fromLegacyText(ChatColor.GREEN + "Votre compte a été créé avec succès, veuillez consulter votre adresse email afin de confirmer sa création !"));
                            break;
                        case "2":
                            sender.sendMessage(TextComponent.fromLegacyText(ChatColor.RED + "Vous possédez déjà un compte."));
                            break;
                        case "3":
                            sender.sendMessage(TextComponent.fromLegacyText(ChatColor.RED + "Cette adresse email est déjà associée à un autre compte."));
                            break;
                        case "4":
                            sender.sendMessage(TextComponent.fromLegacyText(ChatColor.RED + "Vous possédez déjà un compte."));
                            break;
                        default:
                            sender.sendMessage(TextComponent.fromLegacyText(ChatColor.RED + "Une erreur est survenue. :("));
                            System.out.println("[API] Register: code " + result);
                            break;
                    }
                } catch (IOException e) {
                    System.out.println("[API] Register: " + ChatColor.RED + e.getMessage());
                    sender.sendMessage(TextComponent.fromLegacyText(ChatColor.RED + "Une erreur est survenue. :-("));
                    // Ne devrait pas survenir, sauf en cas d'erreur. :D
                }

            }
        });
    }
}
