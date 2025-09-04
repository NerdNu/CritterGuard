package me.ppgome.critterGuard;

import me.ppgome.critterGuard.database.MountAccess;
import me.ppgome.critterGuard.database.SavedMount;
import org.bukkit.OfflinePlayer;

import java.util.*;

/**
 * This class handles the storage of critters in-memory and provides the necessary
 * methods to accomplish this.
 */
public class CritterCache {

    /**
     * The instance of the CritterGuard plugin.
     */
    CritterGuard plugin;

    /**
     * An in-memory cache of SavedMount objects.
     * This cache is used to store all SavedMounts for quick retrieval.
     */
    private HashMap<UUID, SavedMount> savedMountsCache = new HashMap<>();

    /**s
     * An in-memory cache of PlayerMeta objects.
     * This cache is used to store all PlayerMetas for quick retrieval.
     */
    private HashMap<UUID, PlayerMeta> playerMetaCache = new HashMap<>();

    /**
     * An in-memory cache of awaiting clicks for access requests.
     * This cache is used to store player UUIDs that are waiting for access clicks.
     */
    private HashMap<UUID, MountAccess> accessClickCache = new HashMap<>();

    /**
     * An in-memory cache of awaiting clicks for tame requests.
     * This cache is used to store player UUIDs that are waiting for tame clicks.
     */
    private HashMap<UUID, OfflinePlayer> tameClickCache = new HashMap<>();

    /**
     * An in-memory cache of awaiting clicks for untame requests.
     * This cache is used to store player UUIDs that are waiting for untame clicks.
     */
    private Set<UUID> untameClickCache = new HashSet<>();

    //------------------------------------------------------------------------------------------------------------------

    /**
     * Initializes the plugin's cache.
     * @param plugin The instance of the plugin
     */
    public CritterCache(CritterGuard plugin) {
        this.plugin = plugin;
    }

    //------------------------------------------------------------------------------------------------------------------

    // -------- Saved Mount Cache

    /**
     * Adds a new saved mount to the cache.
     *
     * @param savedMount The mount being added to the cache
     */
    public void addSavedMount(SavedMount savedMount) {
        savedMountsCache.put(savedMount.getEntityUuid(), savedMount);
    }

    /**
     * Fetches the saved mount whom the specified UUID belongs to.
     *
     * @param mountUuid The UUID of the mount
     * @return The mount
     */
    public SavedMount getSavedMount(UUID mountUuid) {
        return savedMountsCache.get(mountUuid);
    }

    /**
     * Removes a saved mount from the cache.
     *
     * @param savedMount The saved mount to be removed
     */
    public void removeSavedMount(SavedMount savedMount) {
        savedMountsCache.remove(savedMount.getEntityUuid());
    }

    // -------- Player Meta Cache

    /**
     * Adds a player's playermeta to the cache.
     *
     * @param playerMeta The player's playermeta
     */
    public void addPlayerMeta(PlayerMeta playerMeta) {
        playerMetaCache.put(playerMeta.getUuid(), playerMeta);
    }

    /**
     * Fetches the player's playermeta whom the specified UUID belongs to.
     *
     * @param playerUuid The UUID of the player whose playermeta is being fetched
     * @return The playermeta
     */
    public PlayerMeta getPlayerMeta(UUID playerUuid) {
        return playerMetaCache.get(playerUuid);
    }

    /**
     * Removes a player's playermeta from the cache.
     *
     * @param playerUuid The UUID of the player whose playermeta is being removed
     */
    public void removePlayerMeta(UUID playerUuid) {
        playerMetaCache.remove(playerUuid);
    }

    // -------- Access Click Cache

    /**
     * Adds a player who is going to be clicking an entity to grant access to another player.
     *
     * @param playerUuid The UUID of the player who will be clicking
     * @param playerGettingAccess The MountAccess instance representing the access that will be added
     */
    public void addAwaitingAccess(UUID playerUuid, MountAccess playerGettingAccess) {
        accessClickCache.put(playerUuid, playerGettingAccess);
    }

    /**
     * Checks if there's a record of the specified player who is in the process of granting access to another player.
     *
     * @param playerUuid The UUID of the player being checked for
     * @return True if they are in the cache, false if not.
     */
    public boolean isAwaitingAccess(UUID playerUuid) {
        return accessClickCache.containsKey(playerUuid);
    }

    /**
     * Returns the value in the map associated to the UUID of the player as specified.
     *
     * @param playerUuid The UUID of the player
     * @return The value in the map
     */
    public MountAccess getAwaitingAccess(UUID playerUuid) {
        MountAccess mountAccess = accessClickCache.get(playerUuid);
        removeAwaitingAccess(playerUuid);
        return mountAccess;
    }

    /**
     * Removes a record from the cache that matches the specified UUID.
     *
     * @param playerUuid the UUID of the player whose record is being removed from the cache
     */
    public void removeAwaitingAccess(UUID playerUuid) {
        accessClickCache.remove(playerUuid);
    }

    // -------- Tame Click Cache

    /**
     * Adds a player who is going to be clicking an entity to grant access to another player.
     *
     * @param playerUuid The UUID of the player who will be clicking
     * @param playerTaming The player who the mount is being tamed to
     */
    public void addAwaitingTame(UUID playerUuid, OfflinePlayer playerTaming) {
        tameClickCache.put(playerUuid, playerTaming);
    }

    /**
     * Checks if there's a record of the specified player who is in the process of taming an entity to another player.
     *
     * @param playerUuid The UUID of the player being checked for
     * @return True if they are in the cache, false if not.
     */
    public boolean isAwaitingTame(UUID playerUuid) {
        return tameClickCache.containsKey(playerUuid);
    }

    /**
     * Returns the value in the map associated to the UUID of the player as specified.
     *
     * @param playerUuid The UUID of the player
     * @return The value in the map
     */
    public OfflinePlayer getAwaitingTame(UUID playerUuid) {
        OfflinePlayer playerTaming = tameClickCache.get(playerUuid);
        removeAwaitingTame(playerUuid);
        return playerTaming;
    }

    /**
     * Removes a record from the cache that matches the specified UUID.
     *
     * @param playerUuid the UUID of the player whose record is being removed from the cache
     */
    public void removeAwaitingTame(UUID playerUuid) {
        tameClickCache.remove(playerUuid);
    }

    // -------- Untame Click Cache

    /**
     * Adds a player who is going to be clicking an entity to untame it.
     *
     * @param playerUuid The UUID of the player who will be clicking
     */
    public void addAwaitingUntame(UUID playerUuid) {
        untameClickCache.add(playerUuid);
    }

    /**
     * Checks if there's a record of the specified player who is in the process of untaming a critter.
     *
     * @param playerUuid The UUID of the player being checked for
     * @return True if they are in the cache, false if not.
     */
    public boolean isAwaitingUntame(UUID playerUuid) {
        return untameClickCache.contains(playerUuid);
    }

    /**
     * Removes a record from the cache that matches the specified UUID.
     *
     * @param playerUuid the UUID of the player whose record is being removed from the cache
     */
    public void removeAwaitingUntame(UUID playerUuid) {
        untameClickCache.remove(playerUuid);
    }

}
