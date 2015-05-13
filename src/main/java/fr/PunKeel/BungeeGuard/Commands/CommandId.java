package fr.PunKeel.BungeeGuard.Commands;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.Collections2;
import fr.PunKeel.BungeeGuard.Main;
import fr.PunKeel.BungeeGuard.Managers.PermissionManager;
import fr.PunKeel.BungeeGuard.Permissions.Group;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public class CommandId extends Command {
    final Joiner joiner;
    private final PermissionManager PM;

    public CommandId(Main plugin) {
        super("id", "bungee.command.id");
        PM = plugin.getPermissionManager();
        joiner = com.google.common.base.Joiner.on(",");

    }

    @Override
    public void execute(net.md_5.bungee.api.CommandSender sender, String[] args) {
        if (!(sender instanceof ProxiedPlayer))
            return;
        UUID uuid = ((ProxiedPlayer) sender).getUniqueId();
        final List<Group> groups = PM.getGroupsWithInherits(uuid);
        Collection<String> groupes = Collections2.transform(groups, new Function<Group, String>() {
            @Override
            public String apply(final Group group) {
                return group.getColor() + group.getName() + net.md_5.bungee.api.ChatColor.RESET + "(" + ChatColor.MAGIC + "111" + ChatColor.RESET + ")";
            }
        });


        sender.sendMessage(TextComponent.fromLegacyText("uid=1000(" + sender.getName() + ") gid=111(minecraft) groups=111(minecraft)," + joiner.join(groupes)));
    }
}

