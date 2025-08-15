package me.ppgome.mountGuard;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import me.ppgome.mountGuard.database.MountAccess;
import me.ppgome.mountGuard.database.MountAccessTable;
import me.ppgome.mountGuard.database.SavedMount;
import me.ppgome.mountGuard.database.SavedMountTable;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * MountGuard is a plugin for managing mount access permissions in Minecraft.
 * It allows players to control who can access their mounts and provides a database
 * to store mount-related information.
 */
public final class MountGuard extends JavaPlugin {

    /**
     * The URL for the SQLite database used by the MountGuard plugin.
     * This database stores information about mount access permissions and saved mounts.
     */
    private static final String DATABASE_URL = "jdbc:sqlite:plugins/MountGuard/MountGuard.db";
    /**
     * The connection source for the SQLite database.
     * This is used to establish connections to the database for performing CRUD operations.
     */
    private ConnectionSource connectionSource;
    /**
     * The Data Access Object (DAO) for managing MountAccess records in the database.
     * This DAO provides methods to perform CRUD operations on MountAccess records.
     */
    private Dao<MountAccess, Integer> mountAccessDao;
    /**
     * The Data Access Object (DAO) for managing SavedMount records in the database.
     * This DAO provides methods to perform CRUD operations on SavedMount records.
     */
    private Dao<SavedMount, String> savedMountDao;
    /**
     * Table handler for MountAccess records.
     * This object provides higher-level methods for managing MountAccess records in the database.
     */
    private MountAccessTable mountAccessTable;
    /**
     * Table handler for SavedMount records.
     * This object provides higher-level methods for managing SavedMount records in the database.
     */
    private SavedMountTable savedMountTable;
    /**
     * An in-memory cache of SavedMount objects.
     * This cache is used to store recently accessed or modified SavedMounts for quick retrieval.
     */
    private HashMap<UUID, SavedMount> savedMountsCache = new HashMap<>();

    private HashMap<UUID, PlayerMeta> playerMetaCache = new HashMap<>();

    //------------------------------------------------------------------------------------------------------------------

    @Override
    public void onEnable() {
        // Plugin startup logic
        setupDatabase();
        loadDatabaseData();
        getServer().getPluginManager().registerEvents(new MGEventHandler(this), this);

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    //------------------------------------------------------------------------------------------------------------------

    /**
     * Sets up the SQLite database for the MountGuard plugin.
     * This method initializes the connection source, creates DAOs for MountAccess and SavedMount,
     * and creates the necessary tables if they do not already exist.
     */
    public void setupDatabase() {
        try {
            // Initialize the connection source
            connectionSource = new JdbcConnectionSource(DATABASE_URL);

            // Create DAOs for MountAccess and SavedMount
            mountAccessDao = DaoManager.createDao(connectionSource, MountAccess.class);
            savedMountDao = DaoManager.createDao(connectionSource, SavedMount.class);

            // Initialize table handlers for MountAccess and SavedMount
            mountAccessTable = new MountAccessTable(this);
            savedMountTable = new SavedMountTable(this);

            // Create tables if they do not exist
            TableUtils.createTableIfNotExists(connectionSource, MountAccess.class);
            TableUtils.createTableIfNotExists(connectionSource, SavedMount.class);

        } catch (SQLException e) {
            logError("Failed to set up database" + e.getMessage());
        }
    }

    public void loadDatabaseData() {

        CompletableFuture<List<SavedMount>> savedMountsFuture = savedMountTable.getAllSavedMounts();
        CompletableFuture<List<MountAccess>> mountAccessFuture = mountAccessTable.getAllMountAccess();

        CompletableFuture.allOf(savedMountsFuture, mountAccessFuture).thenRun(() -> {
            try {
                List<SavedMount> savedMounts = savedMountsFuture.get();
                List<MountAccess> mountAccesses = mountAccessFuture.get();

                // Initialize the in-memory cache for saved mounts
                if(savedMounts != null) {
                    for(SavedMount savedMount : savedMounts) {
                        savedMountsCache.put(UUID.fromString(savedMount.getMountUuid()), savedMount);
                    }
                    logInfo("Loaded " + savedMounts.size() + " saved mounts from the database.");
                }

                if(mountAccesses != null) {
                    for(MountAccess mountAccess : mountAccesses) {
                        UUID playerUuid = UUID.fromString(mountAccess.getPlayerUuid());
                        PlayerMeta playerMeta = playerMetaCache.get(playerUuid);
                        // If player meta is not in cache, create a new one
                        // and add the mount access to it.
                        // This allows us to keep track of which mounts a player has access to.
                        if (playerMeta == null) {
                            playerMeta = new PlayerMeta(playerUuid);
                            playerMetaCache.put(playerUuid, playerMeta);
                        }
                        playerMeta.addMountAccess(mountAccess);
                    }
                }

            } catch (Exception e) {
                logError("Failed to load database data: " + e.getMessage());
            }

        });
    }

    /**
     * Adds a new SavedMount to the in-memory cache and persists it to the database.
     * @param savedMount the SavedMount to register
     */
    public void registerNewSavedMount(SavedMount savedMount) {
        savedMountsCache.put(UUID.fromString(savedMount.getMountUuid()), savedMount);
        savedMountTable.save(savedMount);
    }

    /**
     * Removes a SavedMount from the in-memory cache.
     * @param savedMount the SavedMount to remove from the cache
     */
    public void removeFromSavedMountsCache(SavedMount savedMount) {
        savedMountsCache.remove(savedMount);
    }

    /**
     * Logs an informational message to the server console with a MountGuard prefix.
     * @param message the message to log
     */
    public void logInfo(String message) {
        getComponentLogger().info(Component.text("[MountGuard]" + message));
    }

    /**
     * Logs an error message to the server console with a MountGuard prefix in red color, if supported by the console.
     * @param message the error message to log
     */
    public void logError(String message) {
        getComponentLogger().error(Component.text("[MountGuard]" + message), NamedTextColor.RED);
    }

    //------------------------------------------------------------------------------------------------------------------

    /**
     * Returns the connection source for the SQLite database.
     * This connection source is used to establish connections to the database for performing CRUD operations.
     *
     * @return the connection source for the database
     */
    public ConnectionSource getConnectionSource() {
        return connectionSource;
    }

    /**
     * Returns the Data Access Object (DAO) for managing MountAccess records.
     * This DAO provides methods to perform CRUD operations on MountAccess records.
     *
     * @return the DAO for MountAccess records
     */
    public Dao<MountAccess, Integer> getMountAccessDao() {
        return mountAccessDao;
    }

    /**
     * Returns the Data Access Object (DAO) for managing SavedMount records.
     *
     * @return the DAO for SavedMount records
     */
    public Dao<SavedMount, String> getSavedMountDao() {
        return savedMountDao;
    }

    /**
     * Returns a saved mount by its UUID from the in-memory cache.
     * @param mountUuid the UUID of the mount to retrieve
     * @return the SavedMount object if found, or null if not found
     */
    public SavedMount getSavedMount(UUID mountUuid) {
        return savedMountsCache.get(mountUuid);
    }

}
