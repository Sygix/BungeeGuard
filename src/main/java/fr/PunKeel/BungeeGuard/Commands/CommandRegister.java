package fr.PunKeel.BungeeGuard.Commands;

import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import fr.PunKeel.BungeeGuard.Main;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import java.io.IOException;
import java.util.regex.Pattern;

public class CommandRegister extends Command {
    private static final String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@" + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
    private static final String API_ENDPOINT = "https://forum.uhcgames.com/api_register.php";
    private static final String API_TOKEN = "";

    private final Main plugin;
    private final OkHttpClient httpClient;

    public CommandRegister(Main plugin) {
        super("register", "");
        this.plugin = plugin;
        httpClient = Main.getHttpClient();
    }

    @Override
    public void execute(final CommandSender sender, final String[] args) {
        if (args.length != 2) {
            sender.sendMessage(TextComponent.fromLegacyText(ChatColor.RED + "Usage: /register <email> <mot_de_passe>"));
            return;
        }

        if (!(sender instanceof ProxiedPlayer)) {
            sender.sendMessage(TextComponent.fromLegacyText(ChatColor.RED + "Tu veux que je te " + ChatColor.YELLOW + "console " + ChatColor.RED + " ? >.<"));
            return;
        }

        final ProxiedPlayer p = (ProxiedPlayer) sender;
        final String email = args[0];
        final String password = args[1];
        if (!Pattern.matches(EMAIL_PATTERN, email)) {
            sender.sendMessage(TextComponent.fromLegacyText(ChatColor.RED + "Cette adresse mail semble invalide."));
            return;
        }

        plugin.getProxy().getScheduler().runAsync(plugin, new Runnable() {
            @Override
            public void run() {
                try {
                    RequestBody formBody = new FormEncodingBuilder()
                            .add("token", API_TOKEN)
                            .add("email", email)
                            .add("username", sender.getName())
                            .add("uuid", "" + p.getUniqueId())
                            .add("password", password)
                            .build();
                    Request request = new Request.Builder().url(API_ENDPOINT).post(formBody).build();
                    int code = httpClient.newCall(request).execute().code();
                    switch (code) {
                        case 200:
                            sender.sendMessage(TextComponent.fromLegacyText(ChatColor.GREEN + "Votre compte a été créé avec succès !"));
                            sender.sendMessage(TextComponent.fromLegacyText(ChatColor.AQUA + "Connectez-vous sur " + ChatColor.WHITE + "https://forum.uhcgames.com/"));
                            break;
                        case 410:
                        default:
                            sender.sendMessage(TextComponent.fromLegacyText(ChatColor.RED + "Une erreur est survenue."));
                            if (p.hasPermission("bungee.debug.register")) {
                                p.sendMessage(TextComponent.fromLegacyText(ChatColor.DARK_GRAY + "Code retour: " + ChatColor.GREEN + code));
                            }
                            break;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}