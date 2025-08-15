package me.ppgome.critterGuard.database;

import com.j256.ormlite.dao.Dao;
import me.ppgome.critterGuard.CritterGuard;

import java.util.concurrent.CompletableFuture;

public class SavedPetTable {

    // The DAO for accessing saved mounts in the database.
    private Dao<SavedPet, String> savedPetDao;
    // The instance of the MountGuard plugin.
    private CritterGuard plugin;

    //------------------------------------------------------------------------------------------------------------------

    /**
     * Constructor that initializes the SavedMountTable with the MountGuard plugin instance.
     * @param plugin the instance of the MountGuard plugin.
     */
    public SavedPetTable(CritterGuard plugin) {
        this.plugin = plugin;
        this.savedPetDao = plugin.getSavedPetDao();
    }

    //------------------------------------------------------------------------------------------------------------------

    /**
     * Retrieves a SavedPet by its UUID.
     * @param petUuid the UUID of the pet.
     * @return the SavedPet object, or null if not found.
     */
    public CompletableFuture<SavedPet> getSavedPet(String petUuid) {
    }
    public void delete(SavedPet savedPet) {
        CompletableFuture.runAsync(() -> {
            try {
                savedPetDao.delete(savedPet);
            } catch (Exception e) {
                plugin.logError("Failed to delete pet: " + savedPet.getPetName() + "\n" + e.getMessage());
            }
        });
    }

    public void save(SavedPet savedPet) {
        CompletableFuture.runAsync(() -> {
            try {
                savedPetDao.createOrUpdate(savedPet);
            } catch (Exception e) {
                plugin.logError("Failed to save pet: " + savedPet.getPetName() + "\n" + e.getMessage());
            }
        });
    }

}
