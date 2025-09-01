package me.ppgome.critterGuard;

import me.ppgome.critterGuard.database.MountAccess;
import me.ppgome.critterGuard.database.SavedAnimal;
import me.ppgome.critterGuard.database.SavedMount;
import me.ppgome.critterGuard.database.SavedPet;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;

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

    // List of animals owned by the player.
    private ArrayList<SavedAnimal> ownedList;

    // List of mounts the player has access to.
    private Set<MountAccess> accessList;

    private CritterGuard plugin;

    private CGConfig config;

    private NamespacedKey notificationKey;

    /**
     * Initializes PlayerMeta for a player.
     * @param uuid the UUID of the player.
     */
    public PlayerMeta(UUID uuid, CritterGuard plugin) {
        this.uuid = uuid;
        this.ownedList = new ArrayList<>();
        this.accessList = new java.util.HashSet<>();
        this.plugin = plugin;
        this.config = plugin.getCGConfig();
        notificationKey = new NamespacedKey(plugin, "cg_notif_toggle");
    }

    //------------------------------------------------------------------------------------------------------------------

    /**
     * Checks if the player is the owner of the given SavedAnimal.
     * @param savedAnimal the SavedAnimal to check ownership for.
     * @return true if the player owns the SavedAnimal, false otherwise.
     */
    public boolean isOwnerOf(SavedAnimal savedAnimal) {
        return this.ownedList.contains(savedAnimal);
    }

    public void addOwnedMount(SavedAnimal savedAnimal) {
        this.ownedList.add(savedAnimal);
    }

    public void removeOwnedMount(SavedAnimal savedAnimal) {
        this.ownedList.remove(savedAnimal);
    }

    public void addMountAccess(MountAccess access) {
        this.accessList.add(access);
    }

    public void removeMountAccess(MountAccess access) {
        if(accessList.remove(access)) return;
        for(MountAccess mountAccess : accessList) {
            if(mountAccess.getMountUuid().equals(access.getMountUuid())) {
                accessList.remove(mountAccess);
                return;
            }
        }
    }

    public boolean showNotifications() {
        Player player = Bukkit.getPlayer(uuid);
        if(player == null) return false;
        PersistentDataContainer container = player.getPersistentDataContainer();
        if(container.has(notificationKey, PersistentDataType.BOOLEAN)) {
            return container.get(notificationKey, PersistentDataType.BOOLEAN);
        }
        container.set(notificationKey, PersistentDataType.BOOLEAN, true);
        return true;
    }

    public void toggleNotifications(boolean isEnabling) {
        Player player = Bukkit.getPlayer(uuid);
        if(player == null) return;
        PersistentDataContainer container = player.getPersistentDataContainer();
        if(container.has(notificationKey, PersistentDataType.BOOLEAN)) {
            if(isEnabling) {
                player.sendMessage(config.NOTIFICATION_TOGGLE_ON);
                container.set(notificationKey, PersistentDataType.BOOLEAN, true);
                return;
            }
        }
        player.sendMessage(config.NOTIFICATION_TOGGLE_OFF);
        container.set(notificationKey, PersistentDataType.BOOLEAN, false);
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
    public ArrayList<SavedAnimal> getOwnedList() {
        return ownedList;
    }

    /**
     * Gets the list of mounts the player has access to.
     * @return the list of mount access objects.
     */
    public Set<MountAccess> getAccessList() {
        return accessList;
    }

    /**
     * Retrieves an owned mount by its UUID.
     * @param mountUuid the UUID of the mount to retrieve.
     * @return the SavedAnimal if found, null otherwise.
     */
    public SavedAnimal getOwnedMountByUuid(UUID mountUuid) {
        for(SavedAnimal savedAnimal : ownedList) {
            if(savedAnimal.getEntityUuid().equals(mountUuid)) return savedAnimal;
        }
        return null;
    }

}
