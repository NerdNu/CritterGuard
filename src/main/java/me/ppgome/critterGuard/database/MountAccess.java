package me.ppgome.critterGuard.database;

import com.j256.ormlite.field.DatabaseField;

/**
 * Represents access permissions for mounts in the MountGuard plugin.
 * This class is used to store information about which players have access to which mounts,
 * and whether they have full access or not.
 */
public class MountAccess {

    // The unique identifier for each MountAccess record.
    @DatabaseField(generatedId = true)
    private Integer id;

    // The UUID of the mount this access record pertains to.
    @DatabaseField(canBeNull = false)
    private String mountUuid;

    // The UUID of the player who has access to the mount.
    @DatabaseField(canBeNull = false)
    private String playerUuid;

    // Indicates whether the player has full access to the mount.
    @DatabaseField
    private boolean fullAccess;

    // Indicates whether this MountAccess record is currently being added or removed.
    private boolean beingAdded;

    //------------------------------------------------------------------------------------------------------------------

    /**
     * Default constructor required by ORMLite.
     */
    public MountAccess() {}

    /**
     * Constructs a MountAccess record with the specified mount UUID, player UUID, and access level.
     * @param mountUuid the UUID of the mount.
     * @param playerUuid the UUID of the player.
     * @param fullAccess true if the player has full access, false if only passenger access.
     */
    public MountAccess(String mountUuid, String playerUuid, boolean fullAccess) {
        this.mountUuid = mountUuid;
        this.playerUuid = playerUuid;
        this.fullAccess = fullAccess;
    }

    /**
     * Constructs a MountAccess record with the specified player UUID and access level.
     * This is used when the mount UUID is set later.
     * @param playerUuid the UUID of the player.
     * @param fullAccess true if the player has full access, false if only passenger access.
     */
    public MountAccess(String playerUuid, boolean fullAccess, boolean beingAdded) {
        this.playerUuid = playerUuid;
        this.fullAccess = fullAccess;
        this.beingAdded = beingAdded;
    }

    //------------------------------------------------------------------------------------------------------------------

    /**
     * Returns the unique identifier of this MountAccess record.
     * @return the unique identifier of this MountAccess record.
     */
    public Integer getId() {
        return id;
    }

    /**
     * Sets the unique identifier of this MountAccess record.
     * @param id the unique identifier to set.
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * Returns the UUID of the mount this access record pertains to.
     * @return the UUID of the mount.
     */
    public String getMountUuid() {
        return mountUuid;
    }

    /**
     * Sets the UUID of the mount this access record pertains to.
     * @param mountUuid the UUID of the mount to set.
     */
    public void setMountUuid(String mountUuid) {
        this.mountUuid = mountUuid;
    }

    /**
     * Returns the UUID of the player who has access to the mount.
     * @return the UUID of the player.
     */
    public String getPlayerUuid() {
        return playerUuid;
    }

    /**
     * Sets the UUID of the player who has access to the mount.
     * @param playerUuid the UUID of the player to set.
     */
    public void setPlayerUuid(String playerUuid) {
        this.playerUuid = playerUuid;
    }

    /**
     * Returns whether the player has full access to the mount.
     * @return true if the player has full access, false if only passenger access.
     */
    public boolean isFullAccess() {
        return fullAccess;
    }

    /**
     * Sets whether the player has full access to the mount.
     * @param fullAccess true if the player should have full access, false if only passenger access.
     */
    public void setFullAccess(boolean fullAccess) {
        this.fullAccess = fullAccess;
    }

    /**
     * Returns whether this MountAccess record is currently being added or removed.
     * @return true if this record is being added, false if it is being removed.
     */
    public boolean isBeingAdded() {
        return beingAdded;
    }

}
