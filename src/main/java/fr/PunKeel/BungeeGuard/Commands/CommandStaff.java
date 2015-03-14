package fr.PunKeel.BungeeGuard.Commands;

import com.google.common.base.Joiner;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Ordering;
import com.google.common.primitives.Ints;
import fr.PunKeel.BungeeGuard.Main;
import fr.PunKeel.BungeeGuard.Managers.PermissionManager;
import fr.PunKeel.BungeeGuard.Managers.ServerManager;
import fr.PunKeel.BungeeGuard.MultiBungee.MultiBungee;
import fr.PunKeel.BungeeGuard.Permissions.Group;
import fr.PunKeel.BungeeGuard.Permissions.Permissions;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Command;

import java.util.Collection;
import java.util.UUID;

public class CommandStaff extends Command {
    private final PermissionManager PM;
    // LinkedHashMap: garde l'ordre d'insertion, ce qui fait que les groupes sont affichés dans le bon ordre
    private final MultiBungee MB;
    private final ServerManager SM;
    Ordering<UUID> groupOrderer = new Ordering<UUID>() {
        public int compare(UUID left, UUID right) {
            Group gLeft = PM.getMainGroup(left);
            Group gRight = PM.getMainGroup(right);
            return Ints.compare(gLeft.getWeight(), gRight.getWeight());
        }
    }.reverse();

    public CommandStaff(Main plugin) {
        super("staff", "bungee.command.staff");
        MB = Main.getMB();
        SM = Main.getServerManager();
        PM = plugin.getPermissionManager();
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        TextComponent message = new TextComponent("Staff:");
        message.setColor(ChatColor.RED);

        Collection<UUID> staffMembers = groupOrderer.sortedCopy(Collections2.filter(MB.getPlayersOnline(), new Predicate<UUID>() {
            @Override
            public boolean apply(UUID uuid) {
                return Permissions.hasPerm(uuid, "bungee.in_staff_list");
            }
        }));

        if (staffMembers.isEmpty()) {
            sender.sendMessage(TextComponent.fromLegacyText(ChatColor.RED + "Aucun membre du staff n'est en ligne pour le moment."));
            return;
        }
        TextComponent TC;
        String playerName, serverName;
        Group g;
        for (UUID uuid : staffMembers) {
            playerName = MB.getNameFromUuid(uuid);
            g = PM.getMainGroup(uuid);
            TC = new TextComponent(TextComponent.fromLegacyText(" " + g.getColor() + playerName));
            if (sender.hasPermission("bungee.staff.see")) {
                serverName = MB.getServerFor(uuid).getName();
                TC.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                        new ComponentBuilder("Serveur: ")
                                .append(SM.getPrettyName(serverName) + ChatColor.RESET
                                        + " (" + serverName + ")").color(ChatColor.BLUE)
                                .append("\nProxy: ")
                                .append("" + MB.getProxy(uuid)).color(ChatColor.DARK_AQUA)
                                .append("\nGroupes: ")
                                .append(Joiner.on(", ").join(PM.getGroupes(uuid)))
                                .color(ChatColor.RED)
                                .create()));
            }
            message.addExtra(TC);
        }

        sender.sendMessage(message);
    }
}