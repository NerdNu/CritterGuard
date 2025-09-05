package me.ppgome.critterGuard.commands;

import me.ppgome.critterGuard.CritterGuard;
import me.ppgome.critterGuard.PlayerMeta;
import me.ppgome.critterGuard.database.SavedAnimal;
import org.bukkit.entity.Entity;

import java.util.List;

/**
 * A utility that provides static methods for commands.
 */
public class CommandUtils {

    /**
     * Searches a player's critters to find one that matches the specified identifier.
     * This checks the index numbers of the critters in "/cg list" as well as their names and UUIDs.
     *
     * @param critterIdentifier The identifier that will be checked against the player's critters
     * @param playerMeta The player's playermeta to fetch their critters
     * @param plugin The instance of the plugin
     * @return An entity that matches the identifier. Null if none are found
     */
    public static Entity searchByIdentifier(String critterIdentifier, PlayerMeta playerMeta, CritterGuard plugin) {
        // Check if the identifier is numeric (index-based)
        if (critterIdentifier.matches("\\d+")) {
            // If the identifier is numeric, check if it matches the index
            int index = Integer.parseInt(critterIdentifier) - 1; // Convert to zero-based index
            List<SavedAnimal> ownedList = playerMeta.getOwnedList();
            if (index >= 0 && index < ownedList.size()) {
                SavedAnimal indexedAnimal = ownedList.get(index);
                return plugin.getServer().getEntity(indexedAnimal.getEntityUuid());
            }
        }
        // If not numeric, check for UUID or name match
        for (SavedAnimal savedAnimal : playerMeta.getOwnedList()) {
            if (savedAnimal.getEntityUuid().toString().toLowerCase().startsWith(critterIdentifier) ||
                    (savedAnimal.getEntityName() != null &&
                            savedAnimal.getEntityName().toLowerCase().startsWith(critterIdentifier))) {
                return plugin.getServer().getEntity(savedAnimal.getEntityUuid());
            }
        }
        return null;
    }
}
