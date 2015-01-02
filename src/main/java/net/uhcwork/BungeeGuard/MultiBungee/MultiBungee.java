package net.uhcwork.BungeeGuard.MultiBungee;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.Collections2;
import com.google.common.collect.Multimap;
import com.google.common.primitives.Ints;
import com.imaginarycode.minecraft.redisbungee.RedisBungee;
import com.imaginarycode.minecraft.redisbungee.RedisBungeeAPI;
import com.imaginarycode.minecraft.redisbungee.internal.jedis.Jedis;
import com.imaginarycode.minecraft.redisbungee.internal.jedis.JedisPool;
import com.imaginarycode.minecraft.redisbungee.internal.jedis.exceptions.JedisConnectionException;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.uhcwork.BungeeGuard.Main;
import net.uhcwork.BungeeGuard.Managers.PartyManager;

import java.net.InetAddress;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@SuppressWarnings("UnusedDeclaration")
public class MultiBungee {
    public static final String SEPARATOR = "|\uFAE3|";
    public static final String REGEX_SEPARATOR = "\\|\uFAE3\\|";

    static RedisBungeeAPI api;
    static RedisBungee redisbungee;

    public MultiBungee() {
    }

    public void init() {
        System.out.println("Getting RedisBungee [...]");
        redisbungee = (RedisBungee) ProxyServer.getInstance().getPluginManager().getPlugin("RedisBungee");
        api = RedisBungee.getApi();
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("RedisBungee API: OK.");
    }

    public final int getPlayerCount() {
        return api.getPlayerCount();
    }

    /**
     * Get the last time a player was on. If the player is currently online, this will return 0. If the player has not been recorded,
     * this will return -1. Otherwise it will return a value in milliseconds.
     *
     * @param player a player name
     * @return the last time a player was on, if online returns a 0
     */
    public final long getLastOnline(UUID player) {
        return api.getLastOnline(player);
    }

    /**
     * Get the server where the specified player is playing. This function also deals with the case of local players
     * as well, and will return local information on them.
     *
     * @param player a player name
     * @return a {@link net.md_5.bungee.api.config.ServerInfo} for the server the player is on.
     */
    public final ServerInfo getServerFor(UUID player) {
        if (player == null)
            return null;
        return api.getServerFor(player);
    }

    /**
     * Get the server where the specified player is playing. This function also deals with the case of local players
     * as well, and will return local information on them.
     *
     * @param player a player name
     * @return a {@link net.md_5.bungee.api.config.ServerInfo} for the server the player is on.
     */
    public final ServerInfo getServerFor(String player) {
        return api.getServerFor(api.getUuidFromName(player));
    }

    /**
     * Get a combined list of players on this network.
     * <p/>
     * <strong>Note that this function returns an immutable {@link java.util.Set}.</strong>
     *
     * @return a Set with all players found
     */
    public final Set<UUID> getPlayersOnline() {
        return api.getPlayersOnline();
    }

    /**
     * Get a combined list of players on this network, as a collection of usernames.
     * <p/>
     * <strong>Note that this function returns an immutable {@link java.util.Collection}, and usernames
     * are lazily calculated (but cached, see the contract of {@link #getNameFromUuid(java.util.UUID)}).</strong>
     *
     * @return a Set with all players found
     * @see #getNameFromUuid(java.util.UUID)
     * @since 0.3
     */
    public final Collection<String> getHumanPlayersOnline() {
        return api.getHumanPlayersOnline();
    }

    /**
     * Get a full list of players on all servers.
     *
     * @return a immutable Multimap with all players found on this server
     * @since 0.2.5
     */
    public final Multimap<String, UUID> getServerToPlayers() {
        return api.getServerToPlayers();
    }

    /**
     * Get a list of players on the server with the given name.
     *
     * @param server a server name
     * @return a Set with all players found on this server
     */
    public final Set<UUID> getPlayersOnServer(String server) {
        return api.getPlayersOnServer(server);
    }

    /**
     * Convenience method: Checks if the specified player is online.
     *
     * @param player a player name
     * @return if the player is online
     */
    public final boolean isPlayerOnline(UUID player) {
        return api.isPlayerOnline(player);
    }

    /**
     * Convenience method: Checks if the specified player is online.
     *
     * @param player a player name
     * @return if the player is online
     */
    public final boolean isPlayerOnline(String player) {

        UUID u = getUuidFromName(player);
        return u != null && api.isPlayerOnline(u);
    }

    /**
     * Get the {@link java.net.InetAddress} associated with this player.
     *
     * @param player the player to fetch the IP for
     * @return an {@link java.net.InetAddress} if the player is online, null otherwise
     * @since 0.2.4
     */
    public final InetAddress getPlayerIp(UUID player) {
        return api.getPlayerIp(player);
    }

    /**
     * Get the RedisBungee proxy ID this player is connected to.
     *
     * @param player the player to fetch the IP for
     * @return the proxy the player is connected to, or null if they are offline
     * @since 0.3.3
     */
    public final String getProxy(UUID player) {
        return api.getProxy(player);
    }


    /**
     * Get the RedisBungee proxy ID this player is connected to.
     *
     * @param player the player to fetch the IP for
     * @return the proxy the player is connected to, or null if they are offline
     * @since 0.3.3
     */
    public final String getProxy(String player) {
        return api.getProxy(getUuidFromName(player));
    }

    /**
     * Sends a proxy command to all proxies.
     *
     * @param command the command to send and execute
     * @see #sendProxyCommand(String, String)
     * @since 0.2.5
     */
    public final void sendProxyCommand(String command) {
        api.sendProxyCommand(command);
    }

    /**
     * Sends a proxy command to the proxy with the given ID. "allservers" means all proxies.
     *
     * @param proxyId a proxy ID
     * @param command the command to send and execute
     * @see #getServerId()
     * @see #getAllServers()
     * @since 0.2.5
     */
    public final void sendProxyCommand(String proxyId, String command) {
        api.sendProxyCommand(proxyId, command);
    }

    /**
     * Sends a message to a PubSub channel. The channel has to be subscribed to on this, or another redisbungee instance for {@link com.imaginarycode.minecraft.redisbungee.events.PubSubMessageEvent} to fire.
     *
     * @param channel The PubSub channel
     * @param message the message body to send
     * @since 0.3.3
     */
    public final void sendChannelMessage(String channel, String message) {
        api.sendChannelMessage(channel, message);
    }

    public final void sendChannelMessage(String channel, String... message) {
        sendChannelMessage(channel, Joiner.on(SEPARATOR).join(message));
    }

    /**
     * Get the current BungeeCord server ID for this server.
     *
     * @return the current server ID
     * @see #getAllServers()
     * @since 0.2.5
     */
    public final String getServerId() {
        return api.getServerId();
    }

    /**
     * Get all the linked proxies in this network.
     *
     * @return the list of all proxies
     * @see #getServerId()
     * @since 0.2.5
     */
    public final List<String> getAllServers() {
        return api.getAllServers();
    }

    /**
     * Register (a) PubSub channel(s), so that you may handle {@link com.imaginarycode.minecraft.redisbungee.events.PubSubMessageEvent} for it.
     *
     * @param channels the channels to register
     * @since 0.3
     */
    public final void registerPubSubChannels(String... channels) {
        api.registerPubSubChannels(channels);
    }

    /**
     * Unregister (a) PubSub channel(s).
     *
     * @param channels the channels to unregister
     * @since 0.3
     */
    public final void unregisterPubSubChannels(String... channels) {
        api.unregisterPubSubChannels(channels);
    }

    /**
     * Fetch a name from the specified UUID. UUIDs are cached locally and in Redis. This function falls back to Mojang
     * as a last resort, so calls <strong>may</strong> be blocking.
     * <p/>
     * For the common use case of translating a list of UUIDs into names, use {@link #getHumanPlayersOnline()}
     * as the efficiency of that function is slightly greater as the names are calculated lazily.
     * <p/>
     * If performance is a concern, use {@link #getNameFromUuid(java.util.UUID, boolean)} as this allows you to disable Mojang lookups.
     *
     * @param uuid the UUID to fetch the name for
     * @return the name for the UUID
     * @since 0.3
     */
    public final String getNameFromUuid(UUID uuid) {
        return api.getNameFromUuid(uuid, false);
    }

    /**
     * Fetch a name from the specified UUID. UUIDs are cached locally and in Redis. This function can fall back to Mojang
     * as a last resort if {@code expensiveLookups} is true, so calls <strong>may</strong> be blocking.
     * <p/>
     * For the common use case of translating the list of online players into names, use {@link #getHumanPlayersOnline()}
     * as the efficiency of that function is slightly greater as the names are calculated lazily.
     * <p/>
     * If performance is a concern, set {@code expensiveLookups} to false as this will disable lookups via Mojang.
     *
     * @param uuid             the UUID to fetch the name for
     * @param expensiveLookups whether or not to perform potentially expensive lookups
     * @return the name for the UUID
     * @since 0.3.2
     */
    public final String getNameFromUuid(UUID uuid, boolean expensiveLookups) {
        ProxiedPlayer p = ProxyServer.getInstance().getPlayer(uuid);
        if (p == null || p.getUniqueId() == null)
            return api.getNameFromUuid(uuid, expensiveLookups);
        return p.getName();
    }

    /**
     * Fetch a UUID from the specified name. Names are cached locally and in Redis. This function falls back to Mojang
     * as a last resort, so calls <strong>may</strong> be blocking.
     * <p/>
     * If performance is a concern, see {@link #getUuidFromName(String, boolean)}, which disables the following functions:
     * <ul>
     * <li>Searching local entries case-insensitively</li>
     * <li>Searching Mojang</li>
     * </ul>
     *
     * @param name the UUID to fetch the name for
     * @return the UUID for the name
     * @since 0.3
     */
    public final UUID getUuidFromName(String name) {

        return api.getUuidFromName(name, true);
    }

    /**
     * Fetch a UUID from the specified name. Names are cached locally and in Redis. This function falls back to Mojang
     * as a last resort if {@code expensiveLookups} is true, so calls <strong>may</strong> be blocking.
     * <p/>
     * If performance is a concern, set {@code expensiveLookups} to false to disable searching Mojang and searching for usernames
     * case-insensitively.
     *
     * @param name             the UUID to fetch the name for
     * @param expensiveLookups whether or not to perform potentially expensive lookups
     * @return the UUID for the name
     * @since 0.3.2
     */
    public final UUID getUuidFromName(String name, boolean expensiveLookups) {
        ProxiedPlayer p = ProxyServer.getInstance().getPlayer(name);
        if (p == null || p.getUniqueId() == null)
            return api.getUuidFromName(name, expensiveLookups);
        return p.getUniqueId();
    }

    public final void notifyStaff(String message) {
        sendChannelMessage("notifyStaff", message);
    }

    public void sendPlayerMessage(UUID player, String message) {
        sendChannelMessage("message", "" + player, message);
    }

    public void kickPlayer(String player, String reason) {
        sendChannelMessage("kick", player, reason);
    }

    public void sendPrivateMessage(String senderName, UUID receiverUUID, String message) {
        sendChannelMessage("privateMessage", senderName, "" + receiverUUID, message);
    }

    public void broadcastServers(List<String> serversList, String message) {
        broadcastServers(serversList, message, null);
    }

    public void broadcastServers(List<String> serversList, String message, UUID uuid) {
        broadcastServers(Joiner.on(";").join(serversList), message, uuid);
    }

    public void broadcastServers(String servers, String message) {
        broadcastServers(servers, message, null);
    }

    public void broadcastServers(String servers, String message, UUID sender) {
        if (sender == null)
            sendChannelMessage("broadcast", servers, message, "");
        else
            sendChannelMessage("broadcast", servers, message, "" + sender);
    }

    public void unmutePlayer(UUID muteUUID) {
        sendChannelMessage("unmute", api.getServerId(), "" + muteUUID);
    }

    public void banPlayer(UUID bannedUUID, String bannedName, long bannedUntilTime, String reason, String adminName, UUID adminUUID) {
        sendChannelMessage("ban", api.getServerId(), "" + bannedUUID, bannedName, "" + bannedUntilTime, reason, adminName, "" + adminUUID);

    }

    public void unban(UUID bannedUUID) {
        sendChannelMessage("unban", api.getServerId(), "" + bannedUUID);
    }

    public void staffChat(String server, String sender, String message) {
        sendChannelMessage("staffChat", server, sender, message);
    }

    public void summon(String player, String target, String sender) {
        sendChannelMessage("summon", player, target, sender);
    }

    public void ignorePlayer(UUID uniqueId, char c, UUID toIgnore) {
        if (toIgnore == null)
            sendChannelMessage("ignore", "" + uniqueId, "" + c, "*");
        else
            sendChannelMessage("ignore", "" + uniqueId, "" + c, "" + toIgnore);
    }

    public void requestParties(String server) {
        sendChannelMessage("@" + server + "/partyRequest", getServerId());
    }

    public void replyParties(String server, String data) {
        sendChannelMessage("@" + server + "/partyReply", data);
    }

    public void inviteParty(PartyManager.Party party, UUID joueur) {
        sendChannelMessage("inviteParty", party.getName(), "" + joueur);
    }

    public void addPlayerToParty(PartyManager.Party party, ProxiedPlayer player) {
        addPlayerToParty(party, player.getUniqueId());
    }

    private void addPlayerToParty(PartyManager.Party party, UUID uniqueId) {
        sendChannelMessage("addPartyMember", party.getName(), "" + uniqueId);
    }

    public void setPartyPublique(PartyManager.Party p, boolean publique) {
        sendChannelMessage("setPartyPublique", p.getName(), "" + publique);
    }

    public void playerLeaveParty(PartyManager.Party p, ProxiedPlayer sender) {
        playerLeaveParty(p, sender.getUniqueId());
    }

    private void playerLeaveParty(PartyManager.Party p, UUID uniqueId) {
        sendChannelMessage("playerLeaveParty", p.getName(), "" + uniqueId);
    }

    public void setPartyChat(PartyManager.Party p, UUID uniqueId, boolean isPartyChat) {
        sendChannelMessage("setPartyChat", p.getName(), "" + uniqueId, "" + isPartyChat);
    }

    public void setPartyOwner(PartyManager.Party p, UUID u) {
        sendChannelMessage("setPartyOwner", p.getName(), "" + u);
    }

    public void kickFromParty(PartyManager.Party p, UUID u) {
        sendChannelMessage("kickFromParty", p.getName(), "" + u);
    }

    public void partyChat(String party, UUID uniqueId, String message) {
        sendChannelMessage("partyChat", party, "" + uniqueId, message);
    }

    public void summonParty(String party, String server) {
        sendChannelMessage("summonParty", party, server);
    }

    public void createParty(String nom, ProxiedPlayer owner) {
        sendChannelMessage("createParty", nom, "" + owner.getUniqueId());
    }

    public void silenceServer(String serverName, boolean state) {
        sendChannelMessage("silenceServer", serverName, "" + state);
    }

    public int getPlayersOnProxy(String serverName) {
        JedisPool pool = redisbungee.getPool();
        if (pool != null) {
            Jedis rsc = pool.getResource();
            try {
                return Ints.checkedCast(rsc.scard("proxy:" + serverName + ":usersOnline"));
            } catch (JedisConnectionException e) {
                // Redis server has disappeared!
                pool.returnBrokenResource(rsc);
                throw new RuntimeException("Unable to get total player count", e);
            } finally {
                pool.returnResource(rsc);
            }
        }
        return -1;
    }

    public void disbandParty(String name) {
        sendChannelMessage("disbandParty", name);
    }

    public void invalidatePermissionUser(final UUID u) {
        ProxyServer.getInstance().getScheduler().schedule(Main.plugin, new Runnable() {
            @Override
            public void run() {
                sendChannelMessage("invalidatePermissionUser", "" + u);
            }
        }, 10, TimeUnit.MILLISECONDS);
    }

    public void mutePlayer(UUID muteUUID, String muteName, long muteUntilTime, String reason, String adminName, UUID adminUUID) {
        sendChannelMessage("mute", Main.getMB().getServerId(), "" + muteUUID,
                muteName, "" + muteUntilTime, reason, adminName, "" + adminUUID);
    }

    public RedisBungee getPlugin() {
        return redisbungee;
    }

    public void gtp(String playerName, String to_player) {
        sendChannelMessage("gtp", playerName, to_player);
    }

    public void runCommand(String playerName, String command) {
        sendChannelMessage("runCommand", playerName, command);
    }

    public void replyIgnores(String server, String data) {
        sendChannelMessage("@" + server + "/ignoresReply", data);
    }

    public void requestIgnores(String server) {
        sendChannelMessage("@" + server + "/ignoresRequest", getServerId());
    }

    public void registerPubSubChannels(Set<String> channels) {
        for (String channel : channels) {
            registerPubSubChannels(channel);
        }
    }

    public void setMaintenance(String serverName, boolean isRestricted) {
        sendChannelMessage("maintenance", serverName, isRestricted ? "+" : "-");
    }

    public Collection<String> getNamesFromUuid(Collection<UUID> uuids) {
        return Collections2.transform(uuids, new Function<UUID, String>() {
            @Override
            public String apply(UUID uuid) {
                return getNameFromUuid(uuid);
            }
        });
    }

    public void addFriend(UUID userA, UUID userB) {
        sendChannelMessage("addfriend", "" + userA, "" + userB);
    }

    public void delFriend(UUID userA, UUID userB) {
        sendChannelMessage("delfriend", "" + userA, "" + userB);
    }
}
