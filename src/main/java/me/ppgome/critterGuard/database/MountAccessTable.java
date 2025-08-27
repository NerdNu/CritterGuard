package me.ppgome.critterGuard.database;

import com.j256.ormlite.dao.Dao;
import me.ppgome.critterGuard.CritterGuard;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * The MountAccessTable class is responsible for managing mount access data in the database.
 * It provides methods to interact with the mount access data using the ORMLite library.
 */
public class MountAccessTable {

    // The Dao instance for MountAccess, used to perform database operations.
    private Dao<MountAccess, Integer> mountAccessDao;
    // The instance of the MountGuard plugin, used to access plugin methods and properties.
    private CritterGuard plugin;

    /**
     * Constructs a MountAccessTable instance.
     * @param plugin The MountGuard plugin instance.
     */
    public MountAccessTable(CritterGuard plugin) {
        this.plugin = plugin;
        this.mountAccessDao = plugin.getMountAccessDao();
    }

    /**
     * Fetches the mount access for a specific player asynchronously.
     * @return a CompletableFuture containing an ArrayList of MountAccess objects for the player.
     */
    public CompletableFuture<List<MountAccess>> getAllMountAccess() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return new ArrayList<>(mountAccessDao.queryForAll());
            } catch (Exception e) {
                plugin.logError("Failed to fetch all mount access records: \n" + e.getMessage());
                return new ArrayList<>();
            }
        });
    }

    /**
     * Deletes a mount access relationship from the database asynchronously.
     * @param mountAccess the MountAccess object to be deleted.
     */
    public void delete(MountAccess mountAccess) {
        CompletableFuture.runAsync(() -> {
            try {
                mountAccessDao.delete(mountAccess);
            } catch (Exception e) {
                plugin.getLogger().severe("Failed to delete mount access for user: " + mountAccess.getPlayerUuid()
                        + " on mount: " + mountAccess.getMountUuid());
                e.printStackTrace();
            }
        });
    }

    /**
     * Saves a new or updated mount access relationship to the database asynchronously.
     * @param mountAccess the MountAccess object to be saved.
     */
    public void save(MountAccess mountAccess) {
        CompletableFuture.runAsync(() -> {
            try {
                mountAccessDao.createOrUpdate(mountAccess);
            } catch (Exception e) {
                plugin.logError("Failed to save mount access for " + mountAccess.getPlayerUuid() + " on " +
                        mountAccess.getMountUuid() + ":\n" + e.getMessage());
            }
        });
    }

}
