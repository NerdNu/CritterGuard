package me.ppgome.critterGuard.database;

import com.j256.ormlite.field.DatabaseField;

public class SavedPet extends SavedAnimal {

    @DatabaseField
    private String petSoundType;

    //------------------------------------------------------------------------------------------------------------------

    /**
     * Default constructor required by ORMLite.
     */
    public SavedPet() {}

    /**
     * Constructs a SavedPet record with the specified parameters.
     * Used for recording a wolf.
     * @param petUuid the UUID of the pet.
     * @param petName the name of the pet.
     * @param ownerUuid the UUID of the owner.
     * @param ownerName the name of the owner.
     * @param petType the type of the pet (e.g., horse, llama).
     * @param petSoundType the sound type of the pet (e.g., normal, undead).
     */
    public SavedPet(String petUuid, String petName, String ownerUuid, String ownerName, String petType, String color,
                    String petSoundType) {
        this.entityUuid = petUuid;
        this.entityName = petName;
        this.entityOwnerUuid = ownerUuid;
        this.entityType = petType;
        this.color = color;
        this.petSoundType = petSoundType;
    }

    /**
     * Constructs a SavedPet record with the specified parameters.
     * Used for recording a cat or parrot.
     * @param petUuid the UUID of the pet.
     * @param petName the name of the pet.
     * @param ownerUuid the UUID of the owner.
     * @param ownerName the name of the owner.
     * @param petType the type of the pet (e.g., horse, llama).
     */
    public SavedPet(String petUuid, String petName, String ownerUuid, String ownerName, String petType, String color) {
        this.entityUuid = petUuid;
        this.entityName = petName;
        this.entityOwnerUuid = ownerUuid;
        this.entityType = petType;
        this.color = color;
    }

    //------------------------------------------------------------------------------------------------------------------

    public String getPetSoundType() {
        return petSoundType;
    }

    public void setPetSoundType(String petSoundType) {
        this.petSoundType = petSoundType;
    }
}
