package me.ppgome.critterGuard;

import me.ppgome.critterGuard.database.MountAccess;
import me.ppgome.critterGuard.database.SavedAnimal;
import me.ppgome.critterGuard.database.SavedMount;
import org.bukkit.OfflinePlayer;

import java.util.*;

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

    private HashMap<UUID, OfflinePlayer> tameClickCache = new HashMap<>();

    private Set<UUID> untameClickCache = new HashSet<>();

    //------------------------------------------------------------------------------------------------------------------

    public CritterCache(CritterGuard plugin) {
        this.plugin = plugin;
    }

    //------------------------------------------------------------------------------------------------------------------

    // -------- Saved Mount Cache
    public void addSavedMount(SavedMount savedMount) {
        savedMountsCache.put(savedMount.getEntityUuid(), savedMount);
    }

    public SavedMount getSavedMount(UUID mountUuid) {
        return savedMountsCache.get(mountUuid);
    }

    public void removeSavedMount(SavedMount savedMount) {
        savedMountsCache.remove(savedMount.getEntityUuid());
    }

    // -------- Player Meta Cache

    public void addPlayerMeta(PlayerMeta playerMeta) {
        playerMetaCache.put(playerMeta.getUuid(), playerMeta);
    }

    public PlayerMeta getPlayerMeta(UUID playerUuid) {
        return playerMetaCache.get(playerUuid);
    }

    public void removePlayerMeta(UUID playerUuid) {
        playerMetaCache.remove(playerUuid);
    }

    // -------- Access Click Cache

    public void addAwaitingAccess(UUID playerUuid, MountAccess playerGettingAccess) {
        accessClickCache.put(playerUuid, playerGettingAccess);
    }

    public boolean isAwaitingAccess(UUID playerUuid) {
        return accessClickCache.containsKey(playerUuid);
    }

    public MountAccess getAwaitingAccess(UUID playerUuid) {
        MountAccess mountAccess = accessClickCache.get(playerUuid);
        removeAwaitingAccess(playerUuid);
        return mountAccess;
    }

    public void removeAwaitingAccess(UUID playerUuid) {
        accessClickCache.remove(playerUuid);
    }

    // -------- Tame Click Cache

    public void addAwaitingTame(UUID playerUuid, OfflinePlayer playerTaming) {
        tameClickCache.put(playerUuid, playerTaming);
    }

    public boolean isAwaitingTame(UUID playerUuid) {
        return tameClickCache.containsKey(playerUuid);
    }


    public OfflinePlayer getAwaitingTame(UUID playerUuid) {
        OfflinePlayer playerTaming = tameClickCache.get(playerUuid);
        removeAwaitingTame(playerUuid);
        return playerTaming;
    }

    public void removeAwaitingTame(UUID playerUuid) {
        tameClickCache.remove(playerUuid);
    }

    // -------- Untame Click Cache

    public void addAwaitingUntame(UUID playerUuid) {
        untameClickCache.add(playerUuid);
    }

    public boolean isAwaitingUntame(UUID playerUuid) {
        return untameClickCache.contains(playerUuid);
    }

    public void removeAwaitingUntame(UUID playerUuid) {
        untameClickCache.remove(playerUuid);
    }

}
