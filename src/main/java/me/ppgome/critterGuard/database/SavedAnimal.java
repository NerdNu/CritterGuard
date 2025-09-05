package me.ppgome.critterGuard.database;

import com.j256.ormlite.field.DatabaseField;

import java.util.UUID;

/**
 * This class represents the shared portions of saved mounts and saved pets in the database.
 * These fields are used by all types of entities.
 */
public class SavedAnimal {

    /**
     * The UUID of the mount, used as the primary key in the database.
     */
    @DatabaseField(id = true, canBeNull = false)
    String entityUuid;

    /**
     * The name of the mount.
     */
    @DatabaseField
    String entityName;

    /**
     * The UUID of the mount owner, used to identify who owns the mount.
     */
    @DatabaseField(canBeNull = false)
    String entityOwnerUuid;

    /**
     * The type of the mount entity (e.g. "horse", "camel", etc.).
     */
    @DatabaseField(canBeNull = false)
    String entityType; // Store as EntityType.name()

    /**
     * The color of the entity, applicable for horses, llamas, dogs, cats, and parrots (e.g., "CHESTNUT", "BROWN", etc.).
     */
    @DatabaseField
    String color;

    /**
     * The numeric index of the entity, used for sorting or ordering purposes.
     */
    int index;

    //------------------------------------------------------------------------------------------------------------------

    /**
     * Checks if a player owns this critter.
     *
     * @param playerUuid The UUID of the player who's being checked
     * @return True if they are, false if not
     */
    public boolean isOwner(UUID playerUuid) {
        return entityOwnerUuid.equals(playerUuid.toString());
    }

    /**
     * Gets the UUID of the critter.
     * This is used to uniquely identify the critter in the database.
     * @return the UUID of the critter
     */
    public UUID getEntityUuid() {
        return UUID.fromString(entityUuid);
    }

    /**
     * Sets the UUID of the critter.
     * This is used to uniquely identify the critter in the database.
     * @param mountUuid the UUID of the critter
     */
    public void setEntityUuid(String mountUuid) {
        this.entityUuid = mountUuid;
    }

    /**
     * Gets the name of the critter.
     * This is used to identify the critter in the database and by players.
     * @return the name of the critter
     */
    public String getEntityName() {
        return entityName;
    }

    /**
     * Sets the name of the critter.
     * This is used to identify the critter in the database and by players.
     * @param critterName the name of the critter
     */
    public void setEntityName(String critterName) {
        this.entityName = critterName;
    }

    /**
     * Gets the UUID of the critter's owner.
     * This is used to identify who owns the critter.
     * @return the UUID of the critter owner
     */
    public UUID getEntityOwnerUuid() {
        return UUID.fromString(entityOwnerUuid);
    }

    /**
     * Sets the UUID of the critter's owner.
     * This is used to identify who owns the critter.
     * @param critterOwnerUuid the UUID of the critter owner
     */
    public void setEntityOwnerUuid(String critterOwnerUuid) {
        this.entityOwnerUuid = critterOwnerUuid;
    }

    /**
     * Gets the entity type for the critter.
     * This is used to identify the type of critter (e.g., "horse", "camel", etc.).
     * @return the type of the critter entity
     */
    public String getEntityType() {
        return entityType;
    }

    /**
     * Sets the entity type for the critter.
     * This is used to identify the type of critter (e.g., "horse", "camel", etc.).
     * @param entityType the type of the critter entity
     */
    public void setEntityType(String entityType) {
        this.entityType = entityType;
    }

    /**
     * Gets the color of the critter.
     * @return the color of the critter (e.g., "CHESTNUT", "BROWN", etc.)
     */
    public String getColor() {
        return color;
    }

    /**
     * Sets the color of the critter.
     * @param color the color of the critter (e.g., "CHESTNUT", "BROWN", etc.)
     */
    public void setColor(String color) {
        this.color = color;
    }

    /**
     * Gets the index of the critter.
     * @return the index of the critter
     */
    public int getIndex() {
        return index;
    }

    /**
     * Sets the index of the critter.
     * @param index the index to set for the critter
     */
    public void setIndex(int index) {
        this.index = index;
    }

}
