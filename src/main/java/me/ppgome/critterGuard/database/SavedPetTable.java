package me.ppgome.critterGuard.database;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;
import me.ppgome.critterGuard.CritterGuard;
import org.bukkit.entity.Entity;

import java.sql.SQLException;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * This class provides the methods for interacting with the SavedPet table in the database.
 */
public class SavedPetTable {

    /**
     * The DAO for accessing saved pets in the database.
     */
    private Dao<SavedPet, String> savedPetDao;
    /**
     * The instance of the plugin.
     */
    private CritterGuard plugin;

    //------------------------------------------------------------------------------------------------------------------

    /**
     * Constructor that initializes the SavedPetTable with the plugin instance.
     *
     * @param plugin the instance of the plugin.
     */
    public SavedPetTable(CritterGuard plugin) {
        this.plugin = plugin;
        this.savedPetDao = plugin.getSavedPetDao();
    }

    //------------------------------------------------------------------------------------------------------------------

    /**
     * Retrieves a SavedPet by its UUID.
     *
     * @param petUuid the UUID of the pet.
     * @return the SavedPet object, or null if not found.
     */
    public CompletableFuture<SavedPet> getSavedPet(String petUuid) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return savedPetDao.queryForId(petUuid);
            } catch (Exception e) {
                plugin.logError("Failed to retrieve pet with UUID: " + petUuid + "\n" + e.getMessage());
                return null;
            }
        });
    }

    /**
     * Retrieves all saved pets from the database.
     *
     * @return A list of all saved pets.
     */
    public CompletableFuture<List<SavedPet>> getAllSavedPets() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return savedPetDao.queryForAll();
            } catch (Exception e) {
                plugin.logError("Failed to fetch all saved pets\n" + e.getMessage());
                return null;
            }
        });
    }

    public CompletableFuture<UUID> getOwner(UUID uuid) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                QueryBuilder<SavedPet, String> queryBuilder = savedPetDao.queryBuilder();
                queryBuilder.selectColumns("entityOwnerUuid");
                queryBuilder.where().eq("entityUuid", uuid);

                SavedPet savedPet = savedPetDao.queryForFirst(queryBuilder.prepare());
                if(savedPet != null) {
                    return savedPet.getEntityOwnerUuid();
                }
                return null;
            } catch (Exception e) {
                plugin.logError("Failed to fetch owner of saved pet " + uuid +"\n" + e.getMessage());
                return null;
            }
        });
    }

    /**
     * Removes a saved pet's record from the database.
     *
     * @param savedPet The saved pet being removed
     */
    public void delete(SavedPet savedPet) {
        CompletableFuture.runAsync(() -> {
            try {
                savedPetDao.delete(savedPet);
            } catch (Exception e) {
                plugin.logError("Failed to delete pet: " + savedPet.getEntityUuid() + "\n" + e.getMessage());
            }
        });
    }

    /**
     * Adds/updates a saved pet to the database.
     *
     * @param savedPet The saved pet being saved/updated
     */
    public void save(SavedPet savedPet) {
        CompletableFuture.runAsync(() -> {
            try {
                savedPetDao.createOrUpdate(savedPet);
            } catch (Exception e) {
                plugin.logError("Failed to save pet: " + savedPet.getEntityUuid() + "\n" + e.getMessage());
            }
        });
    }

}
