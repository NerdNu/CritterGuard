package me.ppgome.mountGuard;

import me.ppgome.mountGuard.database.MountAccess;
import me.ppgome.mountGuard.database.SavedMount;

import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

/**
 * PlayerMeta holds metadata for a player, including their UUID and a list of MountAccess objects
 * representing the mounts they have access to. This data can be expanded in the future to include
 * additional player-specific information as needed.
 */
public class PlayerMeta {

    // The UUID of the player.
    private UUID uuid;

    // List of mounts owned by the player.
    private ArrayList<SavedMount> ownedList;

    // List of mounts the player has access to.
    private Set<MountAccess> accessList;

    /**
     * Initializes PlayerMeta for a player.
     * @param uuid the UUID of the player.
     */
    public PlayerMeta(UUID uuid) {
        this.uuid = uuid;
        this.ownedList = new ArrayList<>();
        this.accessList = new java.util.HashSet<>();
    }

    //------------------------------------------------------------------------------------------------------------------

    public void addMountAccess(MountAccess access) {
        this.accessList.add(access);
    }

    //------------------------------------------------------------------------------------------------------------------

    /**
     * Gets the UUID of the player.
     * @return the player's UUID.
     */
    public UUID getUuid() {
        return uuid;
    }

    /**
     * Gets the list of mounts owned by the player.
     * @return the list of owned mounts.
     */
    public ArrayList<SavedMount> getOwnedList() {
        return ownedList;
    }

    /**
     * Gets the list of mounts the player has access to.
     * @return the list of mount access objects.
     */
    public Set<MountAccess> getAccessList() {
        return accessList;
    }

}
