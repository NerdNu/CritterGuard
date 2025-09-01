package me.ppgome.critterGuard;

import io.papermc.paper.event.player.PlayerNameEntityEvent;
import me.ppgome.critterGuard.database.*;
import me.ppgome.critterGuard.utility.CritterAccessHandler;
import me.ppgome.critterGuard.utility.CritterTamingHandler;
import me.ppgome.critterGuard.utility.PlaceholderParser;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;

import java.util.List;
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
    private SavedPetTable savedPetTable;
    private CritterCache critterCache;
    private CritterTamingHandler tamingHandler;
    private CritterAccessHandler accessHandler;

    //------------------------------------------------------------------------------------------------------------------

    public CGEventHandler(CritterGuard plugin) {
        this.plugin = plugin;
        this.config = plugin.getCGConfig();
        this.savedMountTable = plugin.getSavedMountTable();
        this.savedPetTable = plugin.getSavedPetTable();
        this.critterCache = plugin.getCritterCache();
        this.tamingHandler = new CritterTamingHandler(plugin);
        this.accessHandler = new CritterAccessHandler(plugin);
    }

    //------------------------------------------------------------------------------------------------------------------

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        UUID playerUuid = event.getPlayer().getUniqueId();
        PlayerMeta playerMeta = critterCache.getPlayerMeta(playerUuid);
        if(playerMeta == null) {
            critterCache.addPlayerMeta(new PlayerMeta(playerUuid, plugin));
        }
    }

    @EventHandler
    public void onCritterTame(EntityTameEvent event) {
        if(event.getOwner() instanceof OfflinePlayer player) {
            tamingHandler.handleTaming(player, event.getEntity());
        }
    }

    @EventHandler
    public void onCritterDeath(EntityDeathEvent event) {
        Entity entity = event.getEntity();
        if(tamingHandler.isMountableEntity(entity)) {
            SavedMount savedMount = critterCache.getSavedMount(entity.getUniqueId());
            if(savedMount != null && entity.getPassengers().getFirst() instanceof Player player) {
                UUID playerUuid = player.getUniqueId();
                if(savedMount.hasFullAccess(playerUuid) && !savedMount.isOwner(playerUuid)) {
                    notifyPlayer(player, savedMount, config.NOTIFICATION_DIED);
                    plugin.logInfo(player.getName() + " was riding " +
                            savedMount.getEntityOwnerUuid() + "'s mount when it died: " +
                            savedMount.getEntityUuid());
                }
            }
        }
        if(!tamingHandler.canHandleTaming(entity)) return; // Only handle tameable entities
        tamingHandler.processAnimalDeath(entity.getUniqueId());
    }

    @EventHandler
    public void onCritterInteract(PlayerInteractEntityEvent event) {
        Entity entity = event.getRightClicked();
        if (!tamingHandler.canHandleTaming(entity)) return;

        Player player = event.getPlayer();
        UUID playerUuid = player.getUniqueId();
        UUID entityUuid = entity.getUniqueId();
        SavedMount savedMount = critterCache.getSavedMount(entityUuid);

        if (savedMount == null || savedMount.isOwner(playerUuid)) {
            // Not a saved mount, player is the owner, pending access request
            if(critterCache.isAwaitingAccess(playerUuid)) {
                interactAccess(playerUuid, player, savedMount, entityUuid, entity);
                event.setCancelled(true);
            }
            // Not a saved mount, player is the owner, pending tame request
            else if(critterCache.isAwaitingTame(playerUuid)) {
                interactTame(playerUuid, player, entityUuid, entity);
                event.setCancelled(true);
            } else if(critterCache.isAwaitingUntame(playerUuid)) {
                tamingHandler.untame(playerUuid, player, savedMount, entityUuid, entity);
                event.setCancelled(true);
            }
        } else {
            // Player has mount access. Allow passthrough
            if(savedMount.hasAccess(playerUuid)) return;
            // Saved mount, player is not the owner
            else if (!savedMount.hasAccess(playerUuid)) {
                if(config.CAN_BREED_LOCKED_ANIMALS && entity instanceof Animals animal && animal.isAdult()
                        && animal.isBreedItem(player.getActiveItem())) {
                    return; // Allow breeding
                }
                // Player does not have access, prevent interaction
                event.setCancelled(true);
                player.sendMessage(config.PERMISSION_INTERACT);
            }
            // Player clicking is trying to untame the entity
            if(critterCache.isAwaitingUntame(playerUuid)) {
                tamingHandler.untame(playerUuid, player, savedMount, entityUuid, entity);
            }
            event.setCancelled(true);
        }
    }

    private void interactAccess(UUID playerUuid, Player player, SavedMount savedMount, UUID entityUuid, Entity entity) {
        MountAccess mountAccess = critterCache.getAwaitingAccess(playerUuid);
        UUID beingAddedUuid = UUID.fromString(mountAccess.getPlayerUuid());

        if (mountAccess.isFullAccess()) {
            accessHandler.handleFullAccess(player, savedMount, mountAccess, beingAddedUuid, entityUuid);
        } else {
            accessHandler.handlePassengerAccess(player, entity, savedMount, mountAccess, beingAddedUuid, entityUuid);
        }
    }

    private void interactTame(UUID playerUuid, Player player, UUID entityUuid, Entity entity) {
        savedPetTable.getSavedPet(entityUuid.toString()).thenAccept(savedPet -> Bukkit.getScheduler().runTask(plugin, () -> {
            if(savedPet == null) {
                OfflinePlayer playerTaming = critterCache.getAwaitingTame(playerUuid);
                tamingHandler.handleTaming(playerTaming, entity);
                critterCache.removeAwaitingTame(playerUuid);
                player.sendMessage(PlaceholderParser
                        .of(config.TAMING_TO_OTHERS)
                        .player(playerTaming.getName())
                        .parse());
            }
        }));
    }

    @EventHandler
    public void onPlayerNameCritter(PlayerNameEntityEvent event) {
        UUID entityUuid = event.getEntity().getUniqueId(); // The entity being named
        Player player = event.getPlayer(); // The player who is naming the entity
        PlayerMeta playerMeta = critterCache.getPlayerMeta(player.getUniqueId());
        if(event.getName() == null) return; // No name provided

        if(playerMeta != null) {
            SavedAnimal savedAnimal = playerMeta.getOwnedMountByUuid(entityUuid);
            if(savedAnimal != null) {
                String newName = PlainTextComponentSerializer.plainText().serialize(event.getName());
                SavedMount savedMount = critterCache.getSavedMount(entityUuid);
                if(savedMount != null) {
                    savedMount.setEntityName(newName);
                    savedMountTable.save(savedMount);
                } else {
                    savedAnimal.setEntityName(newName);
                    savedPetTable.getSavedPet(entityUuid.toString()).thenAccept(savedPet -> {
                        if(savedPet != null) {
                            savedPet.setEntityName(newName);
                            savedPetTable.save(savedPet);
                        }
                    });
                }
            }
        }
    }

    @EventHandler
    public void onPlayerLeashCritter(PlayerLeashEntityEvent event) {
        Entity entity = event.getEntity();
        if(!(entity instanceof Llama)) return; // Only handle llamas
        Player player = event.getPlayer();
        tamingHandler.handleTaming(player, entity);
    }

    @EventHandler
    public void onCritterMount(EntityMountEvent event) {
        Entity passenger = event.getEntity();
        if(!(passenger instanceof Player player)) return; // Only handle player mounts
        Entity mount = event.getMount();
        UUID mountUuid = mount.getUniqueId();

        // Handle a saved mount
        SavedMount savedMount = critterCache.getSavedMount(mountUuid);
        if(savedMount != null) {
            boolean hasAccess = savedMount.hasAccess(passenger.getUniqueId());
            boolean isFullAccess = savedMount.hasFullAccess(passenger.getUniqueId());
            if(!mount.getPassengers().isEmpty()) {
                // Player has passenger access or is the owner, allow mounting
                if(hasAccess || savedMount.isOwner(passenger.getUniqueId())) return;
                // Player has full access, allow mounting
            } else if(isFullAccess || savedMount.isOwner(passenger.getUniqueId())) {
                if(isFullAccess) {
                    notifyPlayer(player, savedMount, config.NOTIFICATION_MOUNTED);
                    plugin.logInfo(player.getName() + " started riding " +
                            savedMount.getEntityOwnerUuid() + "'s mount: " + savedMount.getEntityUuid());
                }
                return;
            }
            // Player does not have access, prevent mounting
            event.setCancelled(true);
            passenger.sendMessage(config.PERMISSION_MOUNT);
            return;
        }

        if (mount instanceof Camel || mount instanceof HappyGhast) {
            tamingHandler.handleTaming(player, mount);
        }

    }

    public void notifyPlayer(Player player, SavedMount savedMount, String notificationType) {
        Player owner = Bukkit.getPlayer(savedMount.getEntityOwnerUuid());
        // Send message to owner
        if(owner != null && owner.isOnline() &&
                critterCache.getPlayerMeta(owner.getUniqueId()).showNotifications()) {
            String mountString;
            if(savedMount.getEntityName() == null) mountString = String.valueOf(savedMount.getEntityUuid());
            else mountString = savedMount.getEntityName();

            owner.sendMessage(PlaceholderParser
                    .of(notificationType)
                    .player(player.getName())
                    .mount(mountString)
                    .parse());
        }
    }

    @EventHandler
    public void onSneakToggle(PlayerToggleSneakEvent event) {
        if(!event.isSneaking()) return;
        Player player = event.getPlayer();
        Entity mount = player.getVehicle();

        if((!(mount instanceof Camel) && !(mount instanceof HappyGhast))) return;

        List<Entity> passengers = mount.getPassengers();
        if(!accessHandler.isDriver(player, passengers)) return;
        passengers.remove(player);

        SavedMount savedMount = critterCache.getSavedMount(mount.getUniqueId());
        if(savedMount == null) return;
        Entity newDriver = accessHandler.findNewDriver(passengers, savedMount);

        if(newDriver != null) {
            accessHandler.transferControl(mount, passengers, newDriver);
        } else {
            accessHandler.dismountAllPassengers(mount, passengers);
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        Entity vehicle = player.getVehicle();
        if(vehicle != null) {
            SavedMount savedMount = critterCache.getSavedMount(vehicle.getUniqueId());
            if(savedMount != null && savedMount.isOwner(player.getUniqueId())) {
                vehicle.eject();
            }
        }
    }

    @EventHandler
    public void onCritterDamage(EntityDamageEvent event) {
        Entity entity = event.getEntity();
        if(tamingHandler.isMountableEntity(entity)) {
            Vehicle mount = (Vehicle) entity;
            if(mount.getPassengers().isEmpty() && critterCache.getSavedMount(entity.getUniqueId()) != null) {
                event.setCancelled(true);
            }

        } else if(tamingHandler.isPetEntity(entity)) {
            Tameable pet = (Tameable) entity;
            Entity damager = event.getDamageSource().getCausingEntity();
            UUID damagerUuid = null;
            if(damager != null) damagerUuid = damager.getUniqueId();

            if(damagerUuid != null) {
                if(pet.isTamed() && !damagerUuid.equals(pet.getOwnerUniqueId())) {
                    event.setCancelled(true);
                }
            } else {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onCritterDismount(EntityDismountEvent event) {
        Entity entity = event.getDismounted();
        if (tamingHandler.isMountableEntity(entity)) {
            // TODO: REMOVE THIS
            System.out.println("Notify 1");
            SavedMount savedMount = critterCache.getSavedMount(entity.getUniqueId());
            if (savedMount != null && event.getEntity() instanceof Player player) {
                // TODO: REMOVE THIS
                System.out.println("Notify 2");
                UUID playerUUID = player.getUniqueId();
                if (savedMount.hasFullAccess(playerUUID) && !savedMount.isOwner(playerUUID)) {
                    // TODO: REMOVE THIS
                    System.out.println("Notify 3");
                    notifyPlayer(player, savedMount, config.NOTIFICATION_DISMOUNTED);
                    plugin.logInfo(player.getName() + " stopped riding " +
                            savedMount.getEntityOwnerUuid() + "'s mount: " + savedMount.getEntityUuid());
                }
            }
        }
    }
}
