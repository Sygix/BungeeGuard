package fr.PunKeel.BungeeGuard.Commands;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.Collections2;
import fr.PunKeel.BungeeGuard.Main;
import fr.PunKeel.BungeeGuard.Managers.PermissionManager;
import fr.PunKeel.BungeeGuard.Permissions.Group;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Command;

import java.util.Collection;
import java.util.Set;
import java.util.UUID;

public class CommandGroups extends Command {

    private final Main plugin;
    private final PermissionManager PM;

    public CommandGroups(Main plugin) {
        super("groups", "bungee.groups");
        this.plugin = plugin;
        this.PM = plugin.getPermissionManager();
    }


    @Override
    public void execute(final CommandSender sender, String[] args) {
        if (args.length >= 2) {
            sender.sendMessage(TextComponent.fromLegacyText(ChatColor.RED + "Usage: /groups [nom]"));
            return;
        }
        if (args.length == 1) {
            String group = args[0];
            Set<UUID> _users = plugin.getPermissionManager().getUsersInGroup(group);
            Collection<String> users = Collections2.transform(_users, new Function<UUID, String>() {
                @Override
                public String apply(UUID uuid) {
                    return Main.getMB().getNameFromUuid(uuid, true);
                }
            });

            sender.sendMessage(TextComponent.fromLegacyText(ChatColor.GREEN + "Liste des membres de " + ChatColor.BOLD + group + ChatColor.GRAY + "(" + _users.size() + ")"));
            sender.sendMessage(TextComponent.fromLegacyText(Joiner.on(", ").skipNulls().join(users)));
        } else {
            sender.sendMessage(TextComponent.fromLegacyText(ChatColor.GREEN + "Liste des groupes"));
            for (Group g : PM.getGroups().values()) {
                sender.sendMessage(TextComponent.fromLegacyText("- " + g.getColor() + g.getName() + ChatColor.RESET + " (" + g.getId() + ")"));
            }
        }
    }
}
