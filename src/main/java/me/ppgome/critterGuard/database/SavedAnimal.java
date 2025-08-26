package me.ppgome.critterGuard.database;

import com.j256.ormlite.field.DatabaseField;

public class SavedAnimal {

    // The UUID of the mount, used as the primary key in the database.
    @DatabaseField(id = true, canBeNull = false)
    String entityUuid;

    // The name of the mount.
    @DatabaseField
    String entityName;

    // The UUID of the mount owner, used to identify who owns the mount.
    @DatabaseField(canBeNull = false)
    String entityOwnerUuid;

    // The name of the mount owner, used to identify who owns the mount.
    @DatabaseField
    String entityOwnerName;

    // The type of the mount entity (e.g., "horse", "camel", etc.).
    @DatabaseField(canBeNull = false)
    String entityType; // Store as EntityType.name()

    // The color of the entity, applicable for horses, llamas, dogs, cats, and parrots (e.g., "CHESTNUT", "BROWN", etc.).
    @DatabaseField
    String color;

    //------------------------------------------------------------------------------------------------------------------

    /**
     * Gets the UUID of the mount.
     * This is used to uniquely identify the mount in the database.
     * @return the UUID of the mount
     */
    public String getEntityUuid() {
        return entityUuid;
    }

    /**
     * Sets the UUID of the mount.
     * This is used to uniquely identify the mount in the database.
     * @param mountUuid the UUID of the mount
     */
    public void setEntityUuid(String mountUuid) {
        this.entityUuid = mountUuid;
    }

    /**
     * Gets the name of the mount.
     * This is used to identify the mount in the database and by players.
     * @return the name of the mount
     */
    public String getEntityName() {
        return entityName;
    }

    /**
     * Sets the name of the mount.
     * This is used to identify the mount in the database and by players.
     * @param mountName the name of the mount
     */
    public void setEntityName(String mountName) {
        this.entityName = mountName;
    }

    /**
     * Gets the UUID of the mount owner.
     * This is used to identify who owns the mount.
     * @return the UUID of the mount owner
     */
    public String getEntityOwnerUuid() {
        return entityOwnerUuid;
    }

    /**
     * Sets the UUID of the mount owner.
     * This is used to identify who owns the mount.
     * @param mountOwnerUuid the UUID of the mount owner
     */
    public void setEntityOwnerUuid(String mountOwnerUuid) {
        this.entityOwnerUuid = mountOwnerUuid;
    }

    /**
     * Gets the name of the mount owner.
     * This is used to identify who owns the mount.
     * @return the name of the mount owner
     */
    public String setEntityOwnerName() {
        return entityOwnerName;
    }

    /**
     * Sets the name of the mount owner.
     * This is used to identify who owns the mount.
     * @param mountOwnerName the name of the mount owner
     */
    public void setEntityOwnerName(String mountOwnerName) {
        this.entityOwnerName = mountOwnerName;
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

}
