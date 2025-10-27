package me.ppgome.critterGuard.database;

import com.j256.ormlite.dao.Dao;
import me.ppgome.critterGuard.CritterGuard;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * This class provides the methods for interacting with the SavedMount table in the database.
 */
public class SavedMountTable {

    /**
     * The DAO for accessing saved mounts in the database.
     */
    private Dao<SavedMount, String> savedMountDao;
    /**
     * The instance of the plugin.
     */
    private CritterGuard plugin;

    //------------------------------------------------------------------------------------------------------------------

    /**
     * Constructor that initializes the SavedMountTable with the MountGuard plugin instance.
     *
     * @param plugin the instance of the MountGuard plugin.
     */
    public SavedMountTable(CritterGuard plugin) {
        this.plugin = plugin;
        this.savedMountDao = plugin.getSavedMountDao();
    }

    //------------------------------------------------------------------------------------------------------------------

    /**
     * Retrieves all saved mounts from the database asynchronously.
     *
     * @return a CompletableFuture that will contain a list of SavedMount objects.
     */
    public CompletableFuture<List<SavedMount>> getAllSavedMounts() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return savedMountDao.queryForAll();
            } catch (Exception e) {
                plugin.logError("Failed to fetch all saved mounts\n" + e.getMessage());
                return null;
            }
        }).exceptionally(e -> {
            plugin.logError("Failed to fetch all saved mounts\n" + e.getMessage());
            return new ArrayList<>();
        });
    }

    /**
     * Deletes a saved mount record from the database.
     *
     * @param savedMount The saved mount being deleted
     */
    public void delete(SavedMount savedMount) {
        CompletableFuture.runAsync(() -> {
            try {
                savedMountDao.delete(savedMount);
            } catch (Exception e) {
                plugin.getLogger().severe("Failed to delete mount: " + savedMount.getEntityName());
                e.printStackTrace();
            }
        }).exceptionally(e -> {
            plugin.getLogger().severe("Failed to delete mount: " + savedMount.getEntityName());
            return null;
        });
    }

    /**
     * Saves a new or updated mount to the database asynchronously.
     *
     * @param savedMount the SavedMount object to be saved.
     */
    public void save(SavedMount savedMount) {
        CompletableFuture.runAsync(() -> {
            try {
                savedMountDao.createOrUpdate(savedMount);
            } catch (Exception e) {
                plugin.getLogger().severe("Failed to save mount: " + savedMount.getEntityName());
                e.printStackTrace();
            }
        }).exceptionally(e -> {
            plugin.getLogger().severe("Failed to save mount: " + savedMount.getEntityName());
            return null;
        });
    }

}
