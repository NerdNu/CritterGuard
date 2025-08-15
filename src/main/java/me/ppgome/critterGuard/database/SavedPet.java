package me.ppgome.critterGuard.database;

import com.j256.ormlite.field.DatabaseField;

public class SavedPet {

    @DatabaseField(id = true)
    private String petUuid;

    @DatabaseField
    private String petName;

    @DatabaseField(canBeNull = false)
    private String ownerUuid;

    @DatabaseField(canBeNull = false)
    private String ownerName;

    @DatabaseField(canBeNull = false)
    private String petType;

    @DatabaseField
    private String petSoundType;

    //------------------------------------------------------------------------------------------------------------------

    /**
     * Default constructor required by ORMLite.
     */
    public SavedPet() {}

    /**
     * Constructs a SavedPet record with the specified parameters.
     * @param petUuid the UUID of the pet.
     * @param petName the name of the pet.
     * @param ownerUuid the UUID of the owner.
     * @param ownerName the name of the owner.
     * @param petType the type of the pet (e.g., horse, llama).
     * @param petSoundType the sound type of the pet (e.g., normal, undead).
     */
    public SavedPet(String petUuid, String petName, String ownerUuid, String ownerName, String petType, String petSoundType) {
        this.petUuid = petUuid;
        this.petName = petName;
        this.ownerUuid = ownerUuid;
        this.ownerName = ownerName;
        this.petType = petType;
        this.petSoundType = petSoundType;
    }

    //------------------------------------------------------------------------------------------------------------------


    public String getPetUuid() {
        return petUuid;
    }

    public void setPetUuid(String petUuid) {
        this.petUuid = petUuid;
    }

    public String getPetName() {
        return petName;
    }

    public void setPetName(String petName) {
        this.petName = petName;
    }

    public String getOwnerUuid() {
        return ownerUuid;
    }

    public void setOwnerUuid(String ownerUuid) {
        this.ownerUuid = ownerUuid;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public String getPetType() {
        return petType;
    }

    public void setPetType(String petType) {
        this.petType = petType;
    }

    public String getPetSoundType() {
        return petSoundType;
    }

    public void setPetSoundType(String petSoundType) {
        this.petSoundType = petSoundType;
    }
}
