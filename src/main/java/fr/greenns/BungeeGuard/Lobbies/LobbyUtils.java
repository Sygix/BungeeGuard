package fr.greenns.BungeeGuard.Lobbies;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableSortedSet;
import com.google.common.collect.Ordering;
import fr.greenns.BungeeGuard.Main;

import java.util.Collection;

public class LobbyUtils {

    public Main plugin;
    Predicate<Lobby> isOnline = new Predicate<Lobby>() {
        public boolean apply(Lobby lobby) {
            return lobby != null && lobby.isOnline();
        }
    };
    Function<Lobby, Double> getScoreFunction = new Function<Lobby, Double>() {
        public Double apply(Lobby lobby) {
            double score = (lobby.getMaxPlayers() / 2 - lobby.getOnlinePlayers()) * lobby.getTps();
            System.out.println(lobby);
            System.out.println(lobby.getName() + ": score: " + score);
            return score;
            // Formule magique qui renvoie un score selon le nombre de joueurs et le tps.
        }
    };

    public LobbyUtils(Main plugin) {
        this.plugin = plugin;
    }

    public Lobby getLobby(String servername) {
        for (Lobby Lobby : plugin.lobbys) {
            if (Lobby.getName().equals(servername))
                return Lobby;
        }
        return null;
    }

    public Lobby bestLobbyTarget() {
        Collection<Lobby> lobbies = Collections2.filter(plugin.lobbys, isOnline);
        Ordering<Lobby> scoreOrdering = Ordering.natural().onResultOf(getScoreFunction);
        ImmutableSortedSet<Lobby> sortedLobbies = ImmutableSortedSet.orderedBy(scoreOrdering).addAll(lobbies).build();

        return sortedLobbies.descendingSet().first();
    }
}
