package me.ppgome.critterGuard.utility;

import me.ppgome.critterGuard.CGConfig;
import me.ppgome.critterGuard.CritterCache;
import me.ppgome.critterGuard.CritterGuard;
import me.ppgome.critterGuard.PlayerMeta;
import me.ppgome.critterGuard.database.*;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.*;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class CritterTamingHandler {

    private final CritterGuard plugin;
    private final CGConfig config;
    private final SavedMountTable savedMountTable;
    private final MountAccessTable mountAccessTable;
    private final SavedPetTable savedPetTable;
    private final CritterCache critterCache;

    public CritterTamingHandler(CritterGuard plugin) {
        this.plugin = plugin;
        this.config = plugin.getCGConfig();
        this.savedMountTable = plugin.getSavedMountTable();
        this.mountAccessTable = plugin.getMountAccessTable();
        this.savedPetTable = plugin.getSavedPetTable();
        this.critterCache = plugin.getCritterCache();
    }

    public void handleTaming(OfflinePlayer player, Entity entity) {

        String entityId = entity.getUniqueId().toString();
        String customName = entity.customName() != null ? entity.customName().toString() : null;
        String tamerId = player.getUniqueId().toString();
        String tamerName = player.getName();
        String entityType = entity.getType().toString();

        if (isMountableEntity(entity)) {
            handleMountTaming(entity, entityId, customName, tamerId, tamerName, entityType, player);
        } else if (isPetEntity(entity)) {
            handlePetTaming(entity, entityId, customName, tamerId, tamerName, entityType, player);
        }
    }

    /**
     * Handles taming of mountable entities (horses, llamas).
     */
    private void handleMountTaming(Entity entity, String entityId, String customName,
                                   String tamerId, String tamerName, String entityType, OfflinePlayer player) {
        SavedMount newMount;

        switch (entity) {
            case Horse horse:
                newMount = new SavedMount(entityId, customName, tamerId, tamerName,
                        entityType, horse.getColor().toString(), horse.getStyle().toString());
                break;

            case Llama llama:
                newMount = new SavedMount(entityId, customName, tamerId, tamerName,
                        entityType, llama.getColor().toString());
                break;

            case AbstractHorse ignored:
                newMount = new SavedMount(entityId, customName, tamerId, tamerName, entityType);
                break;

            case HappyGhast ignored:
                newMount = new SavedMount(entityId, customName, tamerId, tamerName, entityType);
                break;

            default:
                return; // Unsupported mount type
        }

        registerNewSavedAnimal(newMount);
        if(entity instanceof Tameable tameable) tameable.setOwner(player);

        if(player.isOnline()) {
            Bukkit.getPlayer(player.getUniqueId()).sendMessage(config.TAMING_TO_THEMSELVES);
        }
    }

    /**
     * Handles taming of pet entities (wolves, cats, parrots).
     */
    private void handlePetTaming(Entity entity, String entityId, String customName,
                                 String tamerId, String tamerName, String entityType, OfflinePlayer player) {
        SavedPet savedPet;

        switch (entity) {
            case Wolf wolf:
                savedPet = new SavedPet(entityId, customName, tamerId, tamerName, entityType,
                        wolf.getVariant().getKey().getKey(), wolf.getSoundVariant().getKey().getKey());
                break;
            case Cat cat:
                savedPet = new SavedPet(entityId, customName, tamerId, tamerName,
                        entityType, cat.getCatType().getKey().getKey());
                break;
            case Parrot parrot:
                savedPet = new SavedPet(entityId, customName, tamerId, tamerName,
                        entityType, parrot.getVariant().toString());
                break;
            default:
                return; // Unsupported pet type
        }
        registerNewSavedAnimal(savedPet);
        ((Tameable) entity).setOwner(player);
        if(player.isOnline()) {
            Bukkit.getPlayer(player.getUniqueId()).sendMessage(config.TAMING_TO_THEMSELVES);
        }
    }

    public boolean isMountableEntity(Entity entity) {
        return entity instanceof AbstractHorse || entity instanceof HappyGhast;
    }

    public boolean isPetEntity(Entity entity) {
        return entity instanceof Wolf || entity instanceof Cat || entity instanceof Parrot;
    }

    public boolean canHandleTaming(Entity entity) {
        return isMountableEntity(entity) || isPetEntity(entity);
    }



    /**
     * Adds a new SavedMount to the in-memory cache and persists it to the database.
     * @param savedAnimal the SavedAnimal to register
     */
    public void registerNewSavedAnimal(@NotNull SavedAnimal savedAnimal) {
        UUID playerUuid = savedAnimal.getEntityOwnerUuid();
        plugin.registerNewPlayer(playerUuid);
        PlayerMeta playerMeta = critterCache.getPlayerMeta(playerUuid);
        savedAnimal.setIndex(playerMeta.getOwnedList().size() + 1);
        playerMeta.addOwnedMount(savedAnimal);
        if(savedAnimal instanceof SavedMount savedMount) {
            critterCache.addSavedMount(savedMount);
            savedMountTable.save(savedMount);
        } else {
            savedPetTable.save((SavedPet) savedAnimal);
        }
    }

    /**
     * Removes a SavedMount from the in-memory cache and the database.
     * @param savedAnimal the SavedAnimal to remove from the cache
     */
    public void unregisterSavedMount(SavedAnimal savedAnimal) {
        critterCache.getPlayerMeta(savedAnimal.getEntityOwnerUuid()).removeOwnedMount(savedAnimal);
        if(savedAnimal instanceof SavedMount savedMount) {
            critterCache.removeSavedMount(savedMount);
            savedMountTable.delete(savedMount);
            for(MountAccess mountAccess : savedMount.getAccessList().values()) {
                mountAccessTable.delete(mountAccess);
            }
        } else {
            savedPetTable.delete((SavedPet) savedAnimal);
        }
    }

    /**
     * Processes the death of an animal by removing its saved data from the cache and database.
     * This method checks if the entity is a saved mount or pet and removes it accordingly.
     * @param entityUuid the UUID of the entity that died
     */
    public void processAnimalDeath(UUID entityUuid) {
        SavedMount savedMount = critterCache.getSavedMount(entityUuid);
        if(savedMount != null) {
            unregisterSavedMount(savedMount);
            plugin.logInfo("Removed saved mount " + savedMount.getEntityUuid() + " due to death.");
        } else {
            savedPetTable.getSavedPet(entityUuid.toString()).thenAccept(savedPet -> {
                if(savedPet != null) {
                    Bukkit.getScheduler().runTask(plugin, () -> {
                        PlayerMeta playerMeta = critterCache.getPlayerMeta(savedPet.getEntityOwnerUuid());
                        if(playerMeta != null) {
                            SavedAnimal realAnimal = playerMeta.getOwnedMountByUuid(entityUuid);
                            if(realAnimal != null) {
                                playerMeta.removeOwnedMount(realAnimal);
                            }
                        }
                        savedPetTable.delete(savedPet);
                        plugin.logInfo("Removed saved pet " + savedPet.getEntityUuid() + " due to death.");
                    });
                }
            });
        }
    }

    public void untame(UUID playerUuid, Player player, SavedMount savedMount, UUID entityUuid, Entity entity) {
        boolean canUntameOwn = player.hasPermission("critterguard.untame.own");
        boolean canUntameOthers = player.hasPermission("critterguard.untame.others");
        // is it a mount?
        if(savedMount != null) {
            if((canUntameOwn && savedMount.isOwner(playerUuid)) || canUntameOthers) {
                if(entity instanceof Tameable tameable) tameable.setTamed(false);
                unregisterSavedMount(savedMount);
                player.sendMessage(config.UNTAME);

            } else player.sendMessage(config.TAMED_NOT_YOURS);
        }
        // Is it a pet?
        else {
            savedPetTable.getSavedPet(entityUuid.toString()).thenAccept(savedPet -> Bukkit.getScheduler().runTask(plugin, () -> {
                if(savedPet != null) {
                    if((canUntameOwn && savedPet.isOwner(playerUuid)) || canUntameOthers) {
                        if(entity instanceof Tameable tameable) tameable.setTamed(false);
                        unregisterSavedMount(savedPet);
                        player.sendMessage(config.UNTAME);

                    } else player.sendMessage(config.TAMED_NOT_YOURS);
                } else {
                    player.sendMessage(config.NOT_TAMED);
                }
            }));
        }
        critterCache.removeAwaitingUntame(playerUuid);
    }

}
