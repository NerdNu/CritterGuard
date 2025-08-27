package me.ppgome.critterGuard;

import me.ppgome.critterGuard.database.MountAccess;
import me.ppgome.critterGuard.database.SavedAnimal;
import me.ppgome.critterGuard.database.SavedMount;

import java.util.HashMap;
import java.util.UUID;

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

    //------------------------------------------------------------------------------------------------------------------

    public CritterCache(CritterGuard plugin) {
        this.plugin = plugin;
    }

    //------------------------------------------------------------------------------------------------------------------

    public void addSavedMount(SavedMount savedMount) {
        savedMountsCache.put(UUID.fromString(savedMount.getEntityUuid()), savedMount);
    }

    public SavedMount getSavedMount(UUID mountUuid) {
        return savedMountsCache.get(mountUuid);
    }

    public void removeSavedMount(SavedMount savedMount) {
        savedMountsCache.remove(UUID.fromString(savedMount.getEntityUuid()));
    }

    public void addPlayerMeta(PlayerMeta playerMeta) {
        playerMetaCache.put(playerMeta.getUuid(), playerMeta);
    }

    public PlayerMeta getPlayerMeta(UUID playerUuid) {
        return playerMetaCache.get(playerUuid);
    }

    public void removePlayerMeta(UUID playerUuid) {
        playerMetaCache.remove(playerUuid);
    }

    public void addAwaitingAccess(UUID playerUuid, MountAccess playerGettingAccess) {
        accessClickCache.put(playerUuid, playerGettingAccess);
    }

    public boolean isAwaitingAccess(UUID playerUuid) {
        return accessClickCache.containsKey(playerUuid);
    }

    public void removeAwaitingAccess(UUID playerUuid) {
        accessClickCache.remove(playerUuid);
    }

    public MountAccess getAwaitingAccess(UUID playerUuid) {
        MountAccess mountAccess = accessClickCache.get(playerUuid);
        removeAwaitingAccess(playerUuid);
        return mountAccess;
    }
}
