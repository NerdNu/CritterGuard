package me.ppgome.critterGuard.commands;

import me.ppgome.critterGuard.CritterGuard;
import me.ppgome.critterGuard.PlayerMeta;
import me.ppgome.critterGuard.database.SavedAnimal;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.entity.Entity;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

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
     * @return An entity that matches the identifier. Null if none are found
     */
    public static SavedAnimal searchByIdentifier(String critterIdentifier, PlayerMeta playerMeta) {
        // Check if the identifier is numeric (index-based)
        if (critterIdentifier.matches("\\d+")) {
            // If the identifier is numeric, check if it matches the index
            int index = Integer.parseInt(critterIdentifier) - 1; // Convert to zero-based index
            List<SavedAnimal> ownedList = playerMeta.getOwnedList();
            if (index >= 0 && index < ownedList.size()) {
                return ownedList.get(index);
            }
        }
        // If not numeric, check for UUID or name match
        for (SavedAnimal savedAnimal : playerMeta.getOwnedList()) {
            if (savedAnimal.getEntityUuid().toString().toLowerCase().startsWith(critterIdentifier) ||
                    (savedAnimal.getEntityName() != null &&
                            savedAnimal.getEntityName().toLowerCase().startsWith(critterIdentifier))) {
                return savedAnimal;
            }
        }
        return null;
    }
}
