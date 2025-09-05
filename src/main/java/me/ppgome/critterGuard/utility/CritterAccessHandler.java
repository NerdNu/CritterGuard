package me.ppgome.critterGuard.utility;

import me.ppgome.critterGuard.CGConfig;
import me.ppgome.critterGuard.CritterCache;
import me.ppgome.critterGuard.CritterGuard;
import me.ppgome.critterGuard.database.*;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Camel;
import org.bukkit.entity.Entity;
import org.bukkit.entity.HappyGhast;
import org.bukkit.entity.Player;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * This class provides the methods relating to access that are required by the plugin.
 */
public class CritterAccessHandler {

    /**
     * The instance of the plugin.
     */
    private final CritterGuard plugin;
    /**
     * The instance of the configuration class.
     */
    private final CGConfig config;
    /**
     * The instance of the SavedMountTable used for interacting with the database.
     */
    private final SavedMountTable savedMountTable;
    /**
     * The instance of the mountAccessTable used for interacting with the database.
     */
    private final MountAccessTable mountAccessTable;
    /**
     * The instance of the CritterCache for interacting with the data stored in-memory.
     */
    private final CritterCache critterCache;

    //------------------------------------------------------------------------------------------------------------------

    /**
     * Initializes the methods needed for handling access through the plugin.
     *
     * @param plugin The instance of the plugin.
     */
    public CritterAccessHandler(CritterGuard plugin) {
        this.plugin = plugin;
        this.config = plugin.getCGConfig();
        this.savedMountTable = plugin.getSavedMountTable();
        this.mountAccessTable = plugin.getMountAccessTable();
        this.critterCache = plugin.getCritterCache();
    }

    //------------------------------------------------------------------------------------------------------------------

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
                                .player(player.getName())
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
                                .player(player.getName())
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
     * Asynchronously fetches the name of the owner of a mount from their UUID.
     * @param ownerUuid The UUID of the owner
     * @return The name of the owner
     */
    public CompletableFuture<String> getOwnerName(UUID ownerUuid) {
        return CompletableFuture.supplyAsync(() -> Bukkit.getOfflinePlayer(ownerUuid).getName());
    }
}
