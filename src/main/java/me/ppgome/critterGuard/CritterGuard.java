package me.ppgome.critterGuard;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import me.ppgome.critterGuard.commands.CritterCommand;
import me.ppgome.critterGuard.database.*;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * MountGuard is a plugin for managing mount access permissions in Minecraft.
 * It allows players to control who can access their mounts and provides a database
 * to store mount-related information.
 */
public final class CritterGuard extends JavaPlugin {

    /**
     * The configuration for the MountGuard plugin.
     * This object holds various settings and options for the plugin.
     */
    CGConfig config;

    /**
     * The URL for the SQLite database used by the MountGuard plugin.
     * This database stores information about mount access permissions and saved mounts.
     */
    private static final String DATABASE_URL = "jdbc:sqlite:plugins/CritterGuard/CritterGuard.db";

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
     * The Data Access Object (DAO) for managing SavedPet records in the database.
     * This DAO provides methods to perform CRUD operations on SavedPet records.
     */
    private Dao<SavedPet, String> savedPetDao;

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
     * Table handler for SavedPet records.
     * This object provides higher-level methods for managing SavedPet records in the database.
     */
    private SavedPetTable savedPetTable;

    /**
     * In-memory cache for storing critter and player metadata.
     * This cache is used to quickly access critter and player information without querying the database.
     */
    private CritterCache critterCache;

    /**
     * Command handler for the /critter command.
     * This object handles the execution of the critter command and its subcommands.
     */
    private CritterCommand critterCommand;

    //------------------------------------------------------------------------------------------------------------------

    @Override
    public void onEnable() {
        // Plugin startup logic
        config = new CGConfig(this);
        setupDatabase();
        loadDatabaseData();
        critterCache = new CritterCache(this);
        getServer().getPluginManager().registerEvents(new CGEventHandler(this), this);
        critterCommand = new CritterCommand(this);
        this.getCommand("critter").setExecutor(critterCommand);

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
            savedPetDao = DaoManager.createDao(connectionSource, SavedPet.class);

            // Initialize table handlers for MountAccess and SavedMount
            mountAccessTable = new MountAccessTable(this);
            savedMountTable = new SavedMountTable(this);
            savedPetTable = new SavedPetTable(this);

            // Create tables if they do not exist
            TableUtils.createTableIfNotExists(connectionSource, MountAccess.class);
            TableUtils.createTableIfNotExists(connectionSource, SavedMount.class);
            TableUtils.createTableIfNotExists(connectionSource, SavedPet.class);

        } catch (SQLException e) {
            logError("Failed to set up database" + e.getMessage());
        }
    }

    /**
     * Loads existing data from the database into the in-memory cache.
     * This method retrieves all SavedMount and MountAccess records from the database
     * and populates the CritterCache with this data for quick access during runtime.
     */
    public void loadDatabaseData() {

        CompletableFuture<List<SavedMount>> savedMountsFuture = savedMountTable.getAllSavedMounts();
        CompletableFuture<List<MountAccess>> mountAccessFuture = mountAccessTable.getAllMountAccess();
        CompletableFuture<List<SavedPet>> savedPetsFuture = savedPetTable.getAllSavedPets();

        CompletableFuture.allOf(savedMountsFuture, mountAccessFuture, savedPetsFuture).thenRun(() -> {
            try {
                List<SavedMount> savedMounts = savedMountsFuture.get();
                List<MountAccess> mountAccesses = mountAccessFuture.get();
                List<SavedPet> savedPets = savedPetsFuture.get();

                // Initialize the in-memory cache for saved mounts
                if(savedMounts != null) {
                    for(SavedMount savedMount : savedMounts) {
                        critterCache.addSavedMount(savedMount);
                        registerNewPlayer(UUID.fromString(savedMount.getEntityOwnerUuid())).addOwnedMount(savedMount);
                    }
                    logInfo("Loaded " + savedMounts.size() + " saved mounts from the database.");
                }

                // Initialize the in-memory cache for mount accesses
                if(mountAccesses != null) {
                    for(MountAccess mountAccess : mountAccesses) {
                        registerNewPlayer(UUID.fromString(mountAccess.getPlayerUuid())).addMountAccess(mountAccess);
                    }
                }

                // Initialize the in-memory cache for saved pets
                if(savedPets != null) {
                    for(SavedPet savedPet : savedPets) {
                        registerNewPlayer(UUID.fromString(savedPet.getEntityOwnerUuid())).addOwnedMount(savedPet);
                    }
                    logInfo("Loaded " + savedPets.size() + " saved pets from the database.");
                }

            } catch (Exception e) {
                logError("Failed to load database data: " + e.getMessage());
            }

        });
    }

    /**
     * Registers a new player by adding their UUID to the in-memory cache.
     * If the player already exists in the cache, this method does nothing.
     * @param playerUuid the UUID of the player to register
     */
    public PlayerMeta registerNewPlayer(UUID playerUuid) {
        PlayerMeta playerMeta = critterCache.getPlayerMeta(playerUuid);
        if(playerMeta == null) {
            playerMeta = new PlayerMeta(playerUuid);
            critterCache.addPlayerMeta(playerMeta);
        }
        return playerMeta;
    }

    /**
     * Adds a new SavedMount to the in-memory cache and persists it to the database.
     * @param savedAnimal the SavedAnimal to register
     */
    public void registerNewSavedAnimal(@NotNull SavedAnimal savedAnimal) {
        UUID playerUuid = UUID.fromString(savedAnimal.getEntityOwnerUuid());
        registerNewPlayer(playerUuid);
        critterCache.getPlayerMeta(playerUuid).addOwnedMount(savedAnimal);
        if(savedAnimal instanceof SavedMount savedMount) {
            critterCache.addSavedMount(savedMount);
            savedMountTable.save(savedMount);
        } else {
            savedPetTable.save((SavedPet) savedAnimal);
        }
    }

    /**
     * Removes a SavedMount from the in-memory cache and the database.
     * @param savedAnimal the SavedAnimal to remove from the cache
     */
    public void unregisterSavedMount(SavedAnimal savedAnimal) {
        critterCache.getPlayerMeta(UUID.fromString(savedAnimal.getEntityOwnerUuid())).removeOwnedMount(savedAnimal);
        if(savedAnimal instanceof SavedMount savedMount) {
            critterCache.removeSavedMount(savedMount);
            savedMountTable.delete(savedMount);
        }else {
            savedPetTable.delete((SavedPet) savedAnimal);
        }
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
     * Returns the configuration object for the MountGuard plugin.
     * This object contains various settings and options for the plugin.
     *
     * @return the configuration object
     */
    public CGConfig getCGConfig() {
        return config;
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
     * Returns the Data Access Object (DAO) for managing SavedPet records.
     *
     * @return the DAO for SavedPet records
     */
    public Dao<SavedPet, String> getSavedPetDao() {
        return savedPetDao;
    }

    public MountAccessTable getMountAccessTable() {
        return mountAccessTable;
    }

    public SavedMountTable getSavedMountTable() {
        return savedMountTable;
    }

    public SavedPetTable getSavedPetTable() {
        return savedPetTable;
    }

    /**
     * Returns the CritterCache instance.
     * @return the CritterCache instance
     */
    public CritterCache getCritterCache() {
        return critterCache;
    }

}
