package me.ppgome.critterGuard.commands;

import me.ppgome.critterGuard.CritterGuard;
import me.ppgome.critterGuard.PlayerMeta;
import me.ppgome.critterGuard.database.SavedAnimal;
import org.bukkit.entity.Entity;

import java.util.List;

public class CommandUtils {

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
