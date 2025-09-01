package me.ppgome.critterGuard.utility;

import me.ppgome.critterGuard.CGConfig;
import me.ppgome.critterGuard.CritterCache;
import me.ppgome.critterGuard.CritterGuard;
import me.ppgome.critterGuard.database.*;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Camel;
import org.bukkit.entity.Entity;
import org.bukkit.entity.HappyGhast;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CritterAccessHandler {

    private final CritterGuard plugin;
    private final CGConfig config;
    private final SavedMountTable savedMountTable;
    private final MountAccessTable mountAccessTable;
    private final CritterCache critterCache;

    public CritterAccessHandler(CritterGuard plugin) {
        this.plugin = plugin;
        this.config = plugin.getCGConfig();
        this.savedMountTable = plugin.getSavedMountTable();
        this.mountAccessTable = plugin.getMountAccessTable();
        this.critterCache = plugin.getCritterCache();
    }

    /**
     * Handles granting or removing access to a mount based on the type of access requested.
     *
     * @param player         The player requesting access.
     * @param savedMount     The SavedMount object associated with the entity.
     * @param mountAccess    The MountAccess object containing access details.
     * @param beingAddedUuid The UUID of the player being added or removed.
     * @param entityUuid     The UUID of the entity being accessed.
     */
    public void handleFullAccess(Player player, SavedMount savedMount,
                                 MountAccess mountAccess, UUID beingAddedUuid, UUID entityUuid) {
        processAccessChange(player, savedMount, mountAccess, beingAddedUuid, entityUuid);
    }

    /**
     * Handles granting or removing passenger access to a mount.
     *
     * @param player         The player requesting access.
     * @param entity         The entity being accessed.
     * @param savedMount     The SavedMount object associated with the mount.
     * @param mountAccess    The MountAccess object containing access details.
     * @param beingAddedUuid The UUID of the player being added or removed.
     * @param entityUuid     The UUID of the entity being accessed.
     */
    public void handlePassengerAccess(Player player, Entity entity, SavedMount savedMount,
                                      MountAccess mountAccess, UUID beingAddedUuid, UUID entityUuid) {
        if (!(entity instanceof Camel || entity instanceof HappyGhast)) {
            player.sendMessage(config.DOES_NOT_SUPPORT_PASSENGERS);
            return;
        }
        processAccessChange(player, savedMount, mountAccess, beingAddedUuid, entityUuid);
    }

    /**
     * Processes the access change for a mount based on the player's request.
     *
     * @param player         The player requesting access.
     * @param savedMount     The SavedMount object associated with the mount.
     * @param mountAccess    The MountAccess object containing access details.
     * @param beingAddedUuid The UUID of the player being added or removed.
     * @param entityUuid     The UUID of the entity being accessed.
     */
    private void processAccessChange(Player player, SavedMount savedMount,
                                     MountAccess mountAccess, UUID beingAddedUuid, UUID entityUuid) {
        boolean hasAccess = savedMount.hasAccess(beingAddedUuid);

        if (mountAccess.isBeingAdded()) {
            if (hasAccess) {
                player.sendMessage(config.ALREADY_HAS_ACCESS);
            } else {
                grantAccess(player, savedMount, mountAccess, beingAddedUuid, entityUuid);
            }
        } else {
            if (hasAccess) {
                removeAccess(player, savedMount, mountAccess, beingAddedUuid);
            } else {
                player.sendMessage(config.ALREADY_HAS_NO_ACCESS);
            }
        }
    }

    /**
     * Grants access to a mount for a player.
     *
     * @param player         The player granting access.
     * @param savedMount     The SavedMount object associated with the mount.
     * @param mountAccess    The MountAccess object containing access details.
     * @param beingAddedUuid The UUID of the player being granted access.
     * @param entityUuid     The UUID of the entity being accessed.
     */
    private void grantAccess(Player player, SavedMount savedMount, MountAccess mountAccess,
                             UUID beingAddedUuid, UUID entityUuid) {
        mountAccess.setMountUuid(entityUuid.toString());
        savedMount.addAccess(beingAddedUuid, mountAccess);
        mountAccessTable.save(mountAccess);
        savedMountTable.save(savedMount);
        critterCache.getPlayerMeta(beingAddedUuid).addMountAccess(mountAccess);

        // Send messages depending on access type
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            OfflinePlayer playerBeingAdded = Bukkit.getOfflinePlayer(beingAddedUuid);
            Bukkit.getScheduler().runTask(plugin, () -> {
                if (mountAccess.isFullAccess()) {
                    player.sendMessage(PlaceholderParser
                            .of(config.TARGET_GRANTED_FULL_ACCESS)
                            .player(playerBeingAdded.getName())
                            .parse());
                    if (playerBeingAdded.isOnline()) {
                        Bukkit.getPlayer(beingAddedUuid).sendMessage(PlaceholderParser
                                .of(config.GRANTED_FULL_ACCESS)
                                .player(playerBeingAdded.getName())
                                .parse());
                    }
                } else {
                    player.sendMessage(PlaceholderParser
                            .of(config.TARGET_GRANTED_PASSENGER_ACCESS)
                            .player(playerBeingAdded.getName())
                            .parse());
                    if (playerBeingAdded.isOnline()) {
                        Bukkit.getPlayer(beingAddedUuid).sendMessage(PlaceholderParser
                                .of(config.GRANTED_PASSENGER_ACCESS)
                                .player(playerBeingAdded.getName())
                                .parse());
                    }
                }
            });
        });
    }

    /**
     * Removes access to a mount for a player.
     *
     * @param player         The player removing access.
     * @param savedMount     The SavedMount object associated with the mount.
     * @param mountAccess    The MountAccess object containing access details.
     * @param beingAddedUuid The UUID of the player whose access is being removed.
     */
    private void removeAccess(Player player, SavedMount savedMount, MountAccess mountAccess,
                              UUID beingAddedUuid) {
        savedMount.removeAccess(beingAddedUuid);
        critterCache.getPlayerMeta(beingAddedUuid).removeMountAccess(mountAccess);

        // Send messages depending on access type
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            OfflinePlayer playerBeingAdded = Bukkit.getOfflinePlayer(beingAddedUuid);
            Bukkit.getScheduler().runTask(plugin, () -> {
                if (mountAccess.isFullAccess()) {
                    player.sendMessage(PlaceholderParser
                            .of(config.TARGET_REVOKED_FULL_ACCESS)
                            .player(playerBeingAdded.getName())
                            .parse());
                    if (playerBeingAdded.isOnline()) {
                        Bukkit.getPlayer(beingAddedUuid).sendMessage(PlaceholderParser
                                .of(config.REVOKED_FULL_ACCESS)
                                .player(playerBeingAdded.getName())
                                .parse());
                    }
                } else {
                    player.sendMessage(PlaceholderParser
                            .of(config.TARGET_REVOKED_PASSENGER_ACCESS)
                            .player(playerBeingAdded.getName())
                            .parse());
                    if (playerBeingAdded.isOnline()) {
                        Bukkit.getPlayer(beingAddedUuid).sendMessage(PlaceholderParser
                                .of(config.REVOKED_PASSENGER_ACCESS)
                                .player(playerBeingAdded.getName())
                                .parse());
                    }
                }
            });
        });
    }

    /**
     * Checks if the given player is the driver of the mount.
     *
     * @param player     The player to check.
     * @param passengers The list of current passengers on the mount.
     * @return true if the player is the driver, false otherwise.
     */
    public boolean isDriver(Player player, List<Entity> passengers) {
        return passengers.size() <= 1 || passengers.getFirst().getUniqueId().equals(player.getUniqueId());
    }

    /**
     * Finds a new driver among the passengers who have control access to the mount.
     *
     * @param passengers The list of current passengers on the mount.
     * @param savedMount The SavedMount object associated with the mount.
     * @return The new driver entity if found, null otherwise.
     */
    public Entity findNewDriver(List<Entity> passengers, SavedMount savedMount) {
        for (Entity passenger : passengers) {
            if (hasControlAccess(passenger, savedMount)) return passenger;
        }
        return null;
    }

    /**
     * Checks if the given entity has control access to the mount.
     *
     * @param entity     The entity to check.
     * @param savedMount The SavedMount object associated with the mount.
     * @return true if the entity has control access, false otherwise.
     */
    public boolean hasControlAccess(Entity entity, SavedMount savedMount) {
        return savedMount.isOwner(entity.getUniqueId()) || savedMount.hasFullAccess(entity.getUniqueId());
    }

    /**
     * Transfers control of the mount to a new driver and reorders the passengers accordingly.
     *
     * @param mount      The mount entity.
     * @param passengers The list of current passengers on the mount.
     * @param newDriver  The entity that will become the new driver.
     */
    public void transferControl(Entity mount, List<Entity> passengers, Entity newDriver) {
        List<Entity> reorderedPassengers = new ArrayList<>();

        reorderedPassengers.add(newDriver);
        passengers.remove(newDriver);
        reorderedPassengers.addAll(passengers);

        Bukkit.getScheduler().runTaskLater(plugin, mount::eject, 3L);
        if (!(newDriver instanceof Player newDriverPlayer)) return;
        Component message = PlaceholderParser.of(config.SEAT_SWAP_SUCCESS).player(newDriverPlayer.getName()).parse();
        for (Entity passenger : reorderedPassengers) {
            Bukkit.getScheduler().runTaskLater(plugin, () -> mount.addPassenger(passenger), 3L);
            passenger.sendMessage(message);
        }
    }

    /**
     * Dismounts all passengers from the mount and notifies them.
     *
     * @param mount      The mount entity.
     * @param passengers The list of current passengers on the mount.
     */
    public void dismountAllPassengers(Entity mount, List<Entity> passengers) {
        for (Entity passenger : passengers) {
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                mount.removePassenger(passenger);
                passenger.sendMessage(config.SEAT_SWAP_FAILURE);
            }, 3L);
        }
    }

}
