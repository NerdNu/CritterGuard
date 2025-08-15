package me.ppgome.mountGuard.database;

import com.j256.ormlite.field.DatabaseField;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.UUID;

/**
 * Represents a saved mount in the database.
 * This class is used to store information about mounts such as horses, camels, llamas, donkeys, mules, and happy ghasts.
 * It includes fields for the mount's UUID, name, owner UUID and name, entity type, lock date, color, style, and access list.
 */
public class SavedMount {

    // The UUID of the mount, used as the primary key in the database.
    @DatabaseField(id = true, columnName = "mount_uuid", canBeNull = false)
    private String mountUuid;

    // The name of the mount.
    @DatabaseField(columnName = "mount_name")
    private String mountName;

    // The UUID of the mount owner, used to identify who owns the mount.
    @DatabaseField(columnName = "mount_owner_uuid", canBeNull = false)
    private String mountOwnerUuid;

    // The name of the mount owner, used to identify who owns the mount.
    @DatabaseField(columnName = "mount_owner_name")
    private String mountOwnerName;

    // The type of the mount entity (e.g., "horse", "camel", etc.).
    @DatabaseField(columnName = "entity_type", canBeNull = false)
    private String entityType; // Store as EntityType.name()

    // The date when the mount was last locked or saved.
    @DatabaseField(columnName = "lock_date")
    private Date lockDate;

    // The color of the mount, applicable for horses and llamas (e.g., "CHESTNUT", "BROWN", etc.).
    @DatabaseField(columnName = "color")
    private String color;

    // The style of the mount, applicable for horses (e.g., "BLACK_DOTS", "WHITE", etc.).
    @DatabaseField(columnName = "style")
    private String style;

    // A list of UUIDs and their corresponding access levels for the mount.
    HashMap<UUID, MountAccess> accessList = new HashMap<>();

    //------------------------------------------------------------------------------------------------------------------

    /**
     * Empty constructor required by ORMLite.
     */
    public SavedMount() {}

    /**
     * Constructor to create a new SavedMount horse instance.
     * @param mountUuid the UUID of the mount
     * @param mountName the name of the mount
     * @param mountOwnerUuid the UUID of the mount owner
     * @param mountOwnerName the name of the mount owner
     * @param entityType the type of the mount entity (e.g., "horse", "camel", etc.)
     * @param color the color of the mount (e.g., "white", "brown", etc.)
     * @param style the style of the mount (e.g., "chestnut", "black", etc.)
     */
    public SavedMount(String mountUuid, String mountName, String mountOwnerUuid, String mountOwnerName,
                      String entityType, String color, String style) {
        this.mountUuid = mountUuid;
        this.mountName = mountName;
        this.mountOwnerUuid = mountOwnerUuid;
        this.mountOwnerName = mountOwnerName;
        this.entityType = entityType;
        this.color = color;
        this.style = style;
        this.lockDate = new Date();
    }

    /**
     * Constructor to create a new SavedMount llama instance.
     * @param mountUuid the UUID of the mount
     * @param mountName the name of the mount
     * @param mountOwnerUuid the UUID of the mount owner
     * @param mountOwnerName the name of the mount owner
     * @param entityType the type of the mount entity (e.g., "horse", "camel", etc.)
     * @param color the color of the mount (e.g., "white", "brown", etc.)
     */
    public SavedMount(String mountUuid, String mountName, String mountOwnerUuid, String mountOwnerName,
                      String entityType, String color) {
        this.mountUuid = mountUuid;
        this.mountName = mountName;
        this.mountOwnerUuid = mountOwnerUuid;
        this.mountOwnerName = mountOwnerName;
        this.entityType = entityType;
        this.color = color;
        this.style = null; // Llamas do not have a style
        this.lockDate = new Date();
    }

    /**
     * Constructor to create a new SavedMount donkey, mule, camel, or happy ghast instance.
     * @param mountUuid the UUID of the mount
     * @param mountName the name of the mount
     * @param mountOwnerUuid the UUID of the mount owner
     * @param mountOwnerName the name of the mount owner
     * @param entityType the type of the mount entity (e.g., "horse", "camel", etc.)
     */
    public SavedMount(String mountUuid, String mountName, String mountOwnerUuid, String mountOwnerName,
                      String entityType) {
        this.mountUuid = mountUuid;
        this.mountName = mountName;
        this.mountOwnerUuid = mountOwnerUuid;
        this.mountOwnerName = mountOwnerName;
        this.entityType = entityType;
        this.color = null; // Donkeys, mules, camels, and ghasts do not have a color
        this.style = null; // Donkeys, mules, camels, and ghasts
        this.lockDate = new Date();
    }

    //------------------------------------------------------------------------------------------------------------------

    /**
     * Gets the UUID of the mount.
     * This is used to uniquely identify the mount in the database.
     * @return the UUID of the mount
     */
    public String getMountUuid() {
        return mountUuid;
    }

    /**
     * Sets the UUID of the mount.
     * This is used to uniquely identify the mount in the database.
     * @param mountUuid the UUID of the mount
     */
    public void setMountUuid(String mountUuid) {
        this.mountUuid = mountUuid;
    }

    /**
     * Gets the name of the mount.
     * This is used to identify the mount in the database and by players.
     * @return the name of the mount
     */
    public String getMountName() {
        return mountName;
    }

    /**
     * Sets the name of the mount.
     * This is used to identify the mount in the database and by players.
     * @param mountName the name of the mount
     */
    public void setMountName(String mountName) {
        this.mountName = mountName;
    }

    /**
     * Gets the UUID of the mount owner.
     * This is used to identify who owns the mount.
     * @return the UUID of the mount owner
     */
    public String getMountOwnerUuid() {
        return mountOwnerUuid;
    }

    /**
     * Sets the UUID of the mount owner.
     * This is used to identify who owns the mount.
     * @param mountOwnerUuid the UUID of the mount owner
     */
    public void setMountOwnerUuid(String mountOwnerUuid) {
        this.mountOwnerUuid = mountOwnerUuid;
    }

    /**
     * Gets the name of the mount owner.
     * This is used to identify who owns the mount.
     * @return the name of the mount owner
     */
    public String getMountOwnerName() {
        return mountOwnerName;
    }

    /**
     * Sets the name of the mount owner.
     * This is used to identify who owns the mount.
     * @param mountOwnerName the name of the mount owner
     */
    public void setMountOwnerName(String mountOwnerName) {
        this.mountOwnerName = mountOwnerName;
    }

    /**
     * Gets the entity type for the mount.
     * This is used to identify the type of mount (e.g., "horse", "camel", etc.).
     * @return the type of the mount entity
     */
    public String getEntityType() {
        return entityType;
    }

    /**
     * Sets the entity type for the mount.
     * This is used to identify the type of mount (e.g., "horse", "camel", etc.).
     * @param entityType the type of the mount entity
     */
    public void setEntityType(String entityType) {
        this.entityType = entityType;
    }

    /**
     * Gets the lock date for the mount.
     * This is the date when the mount was last locked or saved.
     * @return the lock date of the mount
     */
    public Date getLockDate() {
        return lockDate;
    }

    /**
     * Sets the lock date for the mount.
     * This is the date when the mount was last locked or saved.
     * @param lockDate the date to set as the lock date
     */
    public void setLockDate(Date lockDate) {
        this.lockDate = lockDate;
    }

    /**
     * Gets the color of the mount.
     * This is applicable for horses and llamas.
     * @return the color of the mount (e.g., "CHESTNUT", "BROWN", etc.)
     */
    public String getColor() {
        return color;
    }

    /**
     * Sets the color of the mount.
     * This is applicable for horses and llamas.
     * @param color the color of the mount (e.g., "CHESTNUT", "BROWN", etc.)
     */
    public void setColor(String color) {
        this.color = color;
    }

    /**
     * Gets the style of the mount.
     * This is applicable for horses.
     * @return the style of the mount (e.g., "BLACK_DOTS", "WHITE", etc.)
     */
    public String getStyle() {
        return style;
    }

    /**
     * Sets the style of the mount.
     * This is applicable for horses.
     * @param style the style of the mount (e.g., "BLACK_DOTS", "WHITE", etc.)
     */
    public void setStyle(String style) {
        this.style = style;
    }

    /**
     * Adds access for the given UUID to the mount.
     * @param uuid the UUID of the user to add access for
     * @param access the access level to grant to the user
     */
    public void addAccess(UUID uuid, MountAccess access) {
        this.accessList.put(uuid, access);
    }

    /**
     * Removes access for the given UUID from the mount.
     * @param uuid the UUID of the user to remove access for
     */
    public void removeAccess(UUID uuid) {
        this.accessList.remove(uuid);
    }

    /**
     * Checks if the given UUID has access to the mount.
     * @param uuid the UUID of the user to check
     * @return true if the user has access, false otherwise
     */
    public boolean hasAccess(UUID uuid) {
        return this.accessList.containsKey(uuid);
    }

    /**
     * Checks if the given UUID has full access to the mount.
     * Full access means the user can do everything with the mount, including riding, feeding, and interacting.
     * @param uuid the UUID of the user to check
     * @return true if the user has full access, false otherwise
     */
    public boolean hasFullAccess(UUID uuid) {
        MountAccess access = this.accessList.get(uuid);
        return access != null && access.isFullAccess();
    }

}
