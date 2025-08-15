package me.ppgome.critterGuard;

import me.ppgome.critterGuard.database.MountAccess;
import me.ppgome.critterGuard.database.SavedMount;

import java.util.ArrayList;
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

    /**
     * An in-memory cache of PlayerMeta objects.
     * This cache is used to store all PlayerMetas for quick retrieval.
     */
    private HashMap<UUID, PlayerMeta> playerMetaCache = new HashMap<>();

    /**
     * An in-memory cache of players who ran a command that require clicking a critter.
     */
    private HashMap<UUID, ClickReason> awaitingClickCache = new HashMap<>();

    //------------------------------------------------------------------------------------------------------------------

    public CritterCache(CritterGuard plugin) {
        this.plugin = plugin;
    }

    //------------------------------------------------------------------------------------------------------------------

    public void addSavedMount(SavedMount savedMount) {
        savedMountsCache.put(UUID.fromString(savedMount.getMountUuid()), savedMount);
    }

    public SavedMount getSavedMount(UUID mountUuid) {
        return savedMountsCache.get(mountUuid);
    }

    public void removeSavedMount(SavedMount savedMount) {
        savedMountsCache.remove(UUID.fromString(savedMount.getMountUuid()));
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

    public void addAwaitingClick(UUID playerUuid, ClickReason reason) {
        awaitingClickCache.put(playerUuid, reason);
    }

    public void getAwaitingClick(UUID playerUuid) {
        awaitingClickCache.get(playerUuid);
    }

    public void removeAwaitingClick(UUID playerUuid) {
        awaitingClickCache.remove(playerUuid);
    }

    //------------------------------------------------------------------------------------------------------------------
    public enum ClickReason {
        ACCESS,
        UNTAME,
        INFO
    }

}
