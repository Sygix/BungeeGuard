package fr.greenns.BungeeGuard.utils;

import com.google.common.base.Joiner;
import com.google.common.collect.Multimap;
import com.imaginarycode.minecraft.redisbungee.RedisBungee;
import com.imaginarycode.minecraft.redisbungee.RedisBungeeAPI;
import net.md_5.bungee.api.config.ServerInfo;

import java.net.InetAddress;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Part of fr.greenns.BungeeGuard.utils
 * Date: 29/08/2014
 * Time: 23:00
 * May be open-source & be sold (by PunKeel, of course !)
 */
public class MultiBungee {
    public static final String SEPARATOR = "|" + 0x123 + 0x00 + "|";
    RedisBungeeAPI api;

    public MultiBungee() {
        api = RedisBungee.getApi();
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
        return api.isPlayerOnline(getUuidFromName(player));
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
        return api.getNameFromUuid(uuid, expensiveLookups);
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
        return api.getUuidFromName(name, false);
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
        return api.getUuidFromName(name, expensiveLookups);
    }

    public final void notifyStaff(String message) {
        sendChannelMessage("notify", message);
    }

    public void sendPlayerMessage(String player, String message) {
        sendChannelMessage("message", player + SEPARATOR + message);
    }

    public void kickPlayer(String player, String reason) {
        sendChannelMessage("kick", player + SEPARATOR + reason);
    }

    public void broadcastServers(List<String> serversList, String message) {
        sendChannelMessage("broadcast", Joiner.on(";").join(serversList) + SEPARATOR + message);
    }
}
