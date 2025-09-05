package me.ppgome.critterGuard.database;

import com.j256.ormlite.field.DatabaseField;

/**
 * This class represents a Minecraft pet. These are defined as tameable mobs that can't be ridden.
 */
public class SavedPet extends SavedAnimal {

    /**
     * The pet's sound type. Only affects wolves.
     */
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
     * @param petType the type of the pet (e.g., wolf, cat).
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
     * @param petType the type of the pet (e.g., wolf, cat).
     */
    public SavedPet(String petUuid, String petName, String ownerUuid, String ownerName, String petType, String color) {
        this.entityUuid = petUuid;
        this.entityName = petName;
        this.entityOwnerUuid = ownerUuid;
        this.entityType = petType;
        this.color = color;
    }
}
