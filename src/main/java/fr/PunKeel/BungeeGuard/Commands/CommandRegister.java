package fr.PunKeel.BungeeGuard.Commands;

import com.imaginarycode.minecraft.redisbungee.internal.okhttp.*;
import fr.PunKeel.BungeeGuard.Main;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import java.io.IOException;
import java.util.regex.Pattern;

public class CommandRegister extends Command {
    private static final String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@" + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
    private static final String API_ENDPOINT = "https://forum.uhcgames.com/api_register.php";
    private static final String API_TOKEN = "token";

    private final Main plugin;
    private final OkHttpClient httpClient;

    public CommandRegister(Main plugin) {
        super("register", "");
        this.plugin = plugin;
        httpClient = new OkHttpClient();
        Dispatcher dispatcher = new Dispatcher(ProxyServer.getInstance().getScheduler().unsafe().getExecutorService(plugin));
        httpClient.setDispatcher(dispatcher);
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
        if (password.length() < 6) {
            sender.sendMessage(TextComponent.fromLegacyText(ChatColor.RED + "Votre mot de passe doit contenir au moins 6 caractères."));
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

                    if (p.hasPermission("bungee.debug.register"))
                        p.sendMessage(TextComponent.fromLegacyText(ChatColor.DARK_GRAY + "Code retour: " + ChatColor.GREEN + code));

                    switch (code) {
                        case 200:
                            sender.sendMessage(TextComponent.fromLegacyText(ChatColor.GREEN + "Votre compte a été créé avec succès !"));
                            sender.sendMessage(TextComponent.fromLegacyText(ChatColor.AQUA + "Connectez-vous sur " + ChatColor.WHITE + "https://forum.uhcgames.com/"));
                            break;
                        case 413:
                            sender.sendMessage(TextComponent.fromLegacyText(ChatColor.RED + "Il me semble que vous avez déjà un compte."));
                            break;
                        case 410:
                        default:
                            sender.sendMessage(TextComponent.fromLegacyText(ChatColor.RED + "Une erreur est survenue."));
                            break;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
