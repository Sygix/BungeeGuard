package fr.greenns.BungeeGuard.commands;

import fr.greenns.BungeeGuard.BungeeGuard;
import fr.greenns.BungeeGuard.utils.ComponentManager;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;

/**
 * Part of fr.greenns.BungeeGuard.commands (bungeeguard)
 * Date: 08/09/2014
 * Time: 18:27
 * May be open-source & be sold (by mguerreiro, of course !)
 */
public class CommandParty extends Command {
    public BungeeGuard plugin;
    String TAG = "[" + ChatColor.BLUE + "Party" + ChatColor.RESET + "]";

    public CommandParty(BungeeGuard plugin) {
        super("party", "party.help");
        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        sender.sendMessage(ComponentManager.generate(TAG + " Bientot de retour!"));
    }

    /*
    Party:
        nom (String)
        owner (uuid)
        members: List<UUID>
        partyChat = List<UUID>

        Actions:
            create(String nom, UUID owner)
            setOwner(UUID newOwner)
            addPlayer(UUID joueur, boolean notification_joueur, boolean notification_party)
            removePlayer(UUID joueur, boolean notification_joueur, boolean notification_party)
            delete(PartyDeleteReason raison)
            invitePlayer(UUID joueur)
            chat(UUID sender, String message)

            isMember(UUID joueur) -> boolean
            canJoin(UUID joueur) -> boolean


    PartyManager:
        Party: Map<String name, Party p>

        Actions:
            getParty(UUID joueur)
            addToParty(UUID joueur, String partyName)
            togglePartyChat(UUID joueur)
            canJoin(UUID joueur
     */
}
