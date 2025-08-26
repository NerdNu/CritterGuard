package me.ppgome.critterGuard;

import me.ppgome.critterGuard.database.*;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityMountEvent;
import org.bukkit.event.entity.EntityTameEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.UUID;

/**
 * Handles events related to mount management.
 * This class will contain methods to handle various events such as mount spawning,
 * player interactions with mounts, and any other relevant events.
 */
public class CGEventHandler implements Listener {

    private CritterGuard plugin;
    private CGConfig config;
    private SavedMountTable savedMountTable;
    private MountAccessTable mountAccessTable;
    private SavedPetTable savedPetTable;
    private CritterCache critterCache;

    //------------------------------------------------------------------------------------------------------------------

    public CGEventHandler(CritterGuard plugin) {
        this.plugin = plugin;
        this.config = plugin.getCGConfig();
        this.savedMountTable = plugin.getSavedMountTable();
        this.mountAccessTable = plugin.getMountAccessTable();
        this.savedPetTable = plugin.getSavedPetTable();
        this.critterCache = plugin.getCritterCache();
    }

    //------------------------------------------------------------------------------------------------------------------

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        UUID playerUuid = event.getPlayer().getUniqueId();
        PlayerMeta playerMeta = critterCache.getPlayerMeta(playerUuid);
        if(playerMeta == null) {
            critterCache.addPlayerMeta(new PlayerMeta(playerUuid));
        }
    }

    @EventHandler
    public void onCritterTame(EntityTameEvent event) {
        System.out.println("tamed");
        Entity entity = event.getEntity();
        AnimalTamer tamer = event.getOwner();

        if (!(tamer instanceof Player player)) return;

        String entityId = entity.getUniqueId().toString();
        String customName = entity.customName() != null ? entity.customName().toString() : null;
        String tamerId = tamer.getUniqueId().toString();
        String tamerName = tamer.getName();
        String entityType = entity.getType().toString();

        if (entity instanceof Horse horse) {
            SavedMount newMount = new SavedMount(entityId, customName, tamerId, tamerName,
                    entityType, horse.getColor().toString(), horse.getStyle().toString());
            plugin.registerNewSavedAnimal(newMount);
            player.sendMessage(MessageUtil.normalMessage(config.PREFIX, "Mount tamed successfully!"));

        } else if (entity instanceof Llama llama) {
            SavedMount newMount = new SavedMount(entityId, customName, tamerId, tamerName,
                    llama.getColor().toString());
            plugin.registerNewSavedAnimal(newMount);
            player.sendMessage(MessageUtil.normalMessage(config.PREFIX, "Mount tamed successfully!"));

        } else {
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
                    return;
            } // Only handle specific tameable entities
            plugin.registerNewSavedAnimal(savedPet);
            player.sendMessage(MessageUtil.normalMessage(config.PREFIX, "Pet tamed successfully!"));
        }
    }

    @EventHandler
    public void onCritterDeath(EntityDeathEvent event) {
        Entity entity = event.getEntity();
        if(!(entity instanceof Tameable tameable)) return; // Only handle tameable entities
        if(!tameable.isTamed() || !(tameable.getOwner() instanceof Player)) return; // Only handle tamed entities with a player owner
        UUID entityUuid = entity.getUniqueId();
        SavedMount savedMount = critterCache.getSavedMount(entityUuid);
        if(savedMount != null) {
            plugin.unregisterSavedMount(savedMount);
        } else {
        }
    }

    @EventHandler
    public void onCritterInteract(PlayerInteractEntityEvent event) {
        Entity entity = event.getRightClicked();
        if (!(entity instanceof Tameable)) return;

        Player player = event.getPlayer();
        UUID playerUuid = player.getUniqueId();
        UUID entityUuid = entity.getUniqueId();
        SavedMount savedMount = critterCache.getSavedMount(entityUuid);

        if (savedMount == null || !isOwner(playerUuid, savedMount) || !critterCache.isAwaitingAccess(playerUuid)) {
            return;
        }

        MountAccess mountAccess = critterCache.getAwaitingAccess(playerUuid);
        UUID beingAddedUuid = UUID.fromString(mountAccess.getPlayerUuid());

        if (mountAccess.isFullAccess()) {
            handleFullAccess(player, savedMount, mountAccess, beingAddedUuid, entityUuid);
        } else {
            handlePassengerAccess(player, entity, savedMount, mountAccess, beingAddedUuid, entityUuid);
        }

        event.setCancelled(true);
    }

    /**
     * Checks if the player is the owner of the mount.
     *
     * @param playerUuid The UUID of the player.
     * @param savedMount The SavedMount object to check against.
     * @return true if the player is the owner, false otherwise.
     */
    private boolean isOwner(UUID playerUuid, SavedMount savedMount) {
        return playerUuid.equals(UUID.fromString(savedMount.getEntityOwnerUuid()));
    }

    /**
     * Handles granting or removing access to a mount based on the type of access requested.
     *
     * @param player The player requesting access.
     * @param savedMount The SavedMount object associated with the entity.
     * @param mountAccess The MountAccess object containing access details.
     * @param beingAddedUuid The UUID of the player being added or removed.
     * @param entityUuid The UUID of the entity being accessed.
     */
    private void handleFullAccess(Player player, SavedMount savedMount,
                                  MountAccess mountAccess, UUID beingAddedUuid, UUID entityUuid) {
        processAccessChange(player, savedMount, mountAccess, beingAddedUuid, entityUuid);
    }

    /**
     * Handles granting or removing passenger access to a mount.
     *
     * @param player The player requesting access.
     * @param entity The entity being accessed.
     * @param savedMount The SavedMount object associated with the mount.
     * @param mountAccess The MountAccess object containing access details.
     * @param beingAddedUuid The UUID of the player being added or removed.
     * @param entityUuid The UUID of the entity being accessed.
     */
    private void handlePassengerAccess(Player player, Entity entity, SavedMount savedMount,
                                       MountAccess mountAccess, UUID beingAddedUuid, UUID entityUuid) {
        if (!(entity instanceof Camel || entity instanceof HappyGhast)) {
            player.sendMessage(MessageUtil.failedMessage(config.PREFIX,
                    "This mount does not support passenger access."));
            return;
        }
        processAccessChange(player, savedMount, mountAccess, beingAddedUuid, entityUuid);
    }

    /**
     * Processes the access change for a mount based on the player's request.
     *
     * @param player The player requesting access.
     * @param savedMount The SavedMount object associated with the mount.
     * @param mountAccess The MountAccess object containing access details.
     * @param beingAddedUuid The UUID of the player being added or removed.
     * @param entityUuid The UUID of the entity being accessed.
     */
    private void processAccessChange(Player player, SavedMount savedMount,
                                     MountAccess mountAccess, UUID beingAddedUuid, UUID entityUuid) {
        boolean hasAccess = savedMount.hasAccess(beingAddedUuid);

        if (mountAccess.isBeingAdded()) {
            if (hasAccess) {
                player.sendMessage(MessageUtil.failedMessage(config.PREFIX,
                        "Player already has access to this mount."));
            } else {
                grantAccess(player, savedMount, mountAccess, beingAddedUuid, entityUuid);
            }
        } else {
            if (hasAccess) {
                removeAccess(player, savedMount, mountAccess, beingAddedUuid);
            } else {
                player.sendMessage(MessageUtil.failedMessage(config.PREFIX,
                        "Player already doesn't have access to this mount."));
            }
        }
    }

    /**
     * Grants access to a mount for a player.
     *
     * @param player The player granting access.
     * @param savedMount The SavedMount object associated with the mount.
     * @param mountAccess The MountAccess object containing access details.
     * @param beingAddedUuid The UUID of the player being granted access.
     * @param entityUuid The UUID of the entity being accessed.
     */
    private void grantAccess(Player player, SavedMount savedMount, MountAccess mountAccess,
                             UUID beingAddedUuid, UUID entityUuid) {
        mountAccess.setMountUuid(entityUuid.toString());
        savedMount.addAccess(beingAddedUuid, mountAccess);
        mountAccessTable.save(mountAccess);
        savedMountTable.save(savedMount);
        critterCache.getPlayerMeta(beingAddedUuid).addMountAccess(mountAccess);
        player.sendMessage(MessageUtil.normalMessage(config.PREFIX, "Access granted."));
    }

    /**
     * Removes access to a mount for a player.
     *
     * @param player The player removing access.
     * @param savedMount The SavedMount object associated with the mount.
     * @param mountAccess The MountAccess object containing access details.
     * @param beingAddedUuid The UUID of the player whose access is being removed.
     */
    private void removeAccess(Player player, SavedMount savedMount, MountAccess mountAccess,
                              UUID beingAddedUuid) {
        savedMount.removeAccess(beingAddedUuid);
        critterCache.getPlayerMeta(beingAddedUuid).removeMountAccess(mountAccess);
        player.sendMessage(MessageUtil.normalMessage(config.PREFIX, "Access removed."));
    }

    @EventHandler
    public void onEntityMount(EntityMountEvent event) {
        Entity passenger = event.getEntity();
        if(!(passenger instanceof Player player)) return; // Only handle player mounts
        Entity mount = event.getMount();
        UUID mountUuid = mount.getUniqueId();

        // Handle a saved mount
        SavedMount savedMount = critterCache.getSavedMount(mountUuid);
        if(savedMount != null) {
            if(mount.getPassengers().size() > 1) {
                if(savedMount.hasAccess(passenger.getUniqueId())) return; // Player already has access to this mount
            } else if(savedMount.hasFullAccess(passenger.getUniqueId()) || isOwner(passenger.getUniqueId(), savedMount)) return;
            else {
                // Player does not have access, prevent mounting
                event.setCancelled(true);
                passenger.sendMessage(MessageUtil.failedMessage(config.PREFIX, "You do not have permission" +
                        " to mount this entity."));
                return;
            }
        }

        if (mount instanceof Camel || mount instanceof HappyGhast) {
            String customName = mount.customName() != null ? mount.customName().toString() : null;
            SavedMount newMount = new SavedMount(mountUuid.toString(), customName, player.getUniqueId().toString(),
                    player.getName(), mount.getType().toString());
            plugin.registerNewSavedAnimal(newMount);
            player.sendMessage(MessageUtil.normalMessage(config.PREFIX, "Mount tamed successfully!"));
        }

    }

//    @EventHandler
//    public void onSneakToggle(PlayerToggleSneakEvent event) {
//        if(!event.isSneaking()) return;
//        Player player = event.getPlayer();
//        Entity mount = player.getVehicle();
//        if(mount != null) {
//            if(mount instanceof AbstractHorse || mount instanceof HappyGhast) {
//                List<Entity> passengers = new ArrayList<>(mount.getPassengers());
//                List<Entity> modifiedPassengers = new ArrayList<>();
//                modifiedPassengers.add(passengers.getLast());
//                passengers.removeLast();
//                modifiedPassengers.addAll(passengers);
//                Bukkit.getScheduler().runTaskLater(plugin, mount::eject, 2L);
//                Bukkit.getScheduler().runTaskLater(plugin, () -> {
//                    for(Entity passenger : modifiedPassengers) {
//                        mount.addPassenger(passenger);
//                    }
//                }, 2L);
//            }
//        }
//    }
}
