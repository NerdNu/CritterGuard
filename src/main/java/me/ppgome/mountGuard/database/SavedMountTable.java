package me.ppgome.mountGuard.database;

import com.j256.ormlite.dao.Dao;
import me.ppgome.mountGuard.MountGuard;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * This class handles the database operations related to saved mounts.
 * It provides methods to retrieve all saved mounts and to save a new mount.
 */
public class SavedMountTable {

    // The DAO for accessing saved mounts in the database.
    private Dao<SavedMount, String> savedMountDao;
    // The instance of the MountGuard plugin.
    private MountGuard plugin;

    /**
     * Constructor that initializes the SavedMountTable with the MountGuard plugin instance.
     * @param plugin the instance of the MountGuard plugin.
     */
    public SavedMountTable(MountGuard plugin) {
        this.plugin = plugin;
        this.savedMountDao = plugin.getSavedMountDao();
    }

    /**
     * Retrieves all saved mounts from the database asynchronously.
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
        });
    }

    /**
     * Saves a new or updated mount to the database asynchronously.
     * @param savedMount the SavedMount object to be saved.
     */
    public void save(SavedMount savedMount) {
        CompletableFuture.runAsync(() -> {
            try {
                savedMountDao.createOrUpdate(savedMount);
            } catch (Exception e) {
                plugin.getLogger().severe("Failed to save mount: " + savedMount.getMountName());
                e.printStackTrace();
            }
        });
    }

}
