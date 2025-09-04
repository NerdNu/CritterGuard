package me.ppgome.critterGuard.disguisesaddles;

import me.libraryaddict.disguise.DisguiseAPI;
import me.libraryaddict.disguise.disguisetypes.Disguise;
import me.libraryaddict.disguise.utilities.parser.DisguiseParser;
import me.ppgome.critterGuard.CritterGuard;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;

/**
 * This class directly deals with the LibsDisguises API.
 *
 * A good portion of the code here was borrowed from EasyRider.
 */
public class LibsDisguiseProvider {

    /**
     * The instance of the plugin.
     */
    private CritterGuard plugin;

    //------------------------------------------------------------------------------------------------------------------

    /**
     * Initializes the methods used for interacting with the LibsDisguises API.
     *
     * @param plugin The instance of the plugin
     */
    public LibsDisguiseProvider(CritterGuard plugin) {
        this.plugin = plugin;
    }

    //------------------------------------------------------------------------------------------------------------------

    /**
     * Disguises an entity and catches exceptions that may arise.
     *
     * @param entity The entity being disguised
     * @param disguiseString The string containing the disguise information
     * @param players The players that will see the disguised entity
     * @return True if the disguise applied successfully, false otherwise
     */
    public boolean applyDisguise(Entity entity, String disguiseString, Set<Player> players) {
        Disguise disguise = null;

        try {
            disguise = DisguiseParser.parseDisguise(Bukkit.getConsoleSender(), entity, disguiseString);
        } catch (Throwable e) {
            Throwable cause = e.getCause();
            plugin.logError("Error applying disguise \"" + disguiseString + "\" to " + entity.getUniqueId() + ": " +
                    (cause != null ? cause.getMessage() : e.getMessage()));
        }

        if(disguise == null) return false;

        DisguiseAPI.undisguiseToAll(entity);
        DisguiseAPI.disguiseToPlayers(entity, disguise, players);
        return true;
    }

    /**
     * Checks if an entity is disguised to anybody.
     *
     * @param entity The entity being checked
     * @return True if it is disguised to anybody, false if it isn't
     */
    public boolean isDisguised(Entity entity) {
        return DisguiseAPI.isDisguised(entity);
    }

    /**
     * Checks if an entity is disguised to a specific player.
     *
     * @param player The player being checked
     * @param entity The entity being checked
     * @return True if the entity is disguised to the player, false if it isn't
     */
    public boolean isDisguisedToPlayer(Player player, Entity entity) {
        return DisguiseAPI.isDisguised(player, entity);
    }

    /**
     * Removes an entity's disguise for all players.
     *
     * @param entity The entity whose disguise is being removed
     */
    public void removeDisguiseForAll(Entity entity) {
        DisguiseAPI.undisguiseToAll(entity);
    }

    /**
     * Removes the disguise of an entity for a single player.
     *
     * @param entity The entity whose disguise is being removed
     * @param player The player who is losing visibility of the disguise
     * @param disguiseString The string containing the disguise's information
     * @param playerSet The players who should still be seeing the disguise
     */
    public void removeDisguiseForOne(Entity entity, Player player, String disguiseString, Set<Player> playerSet) {
        removeDisguiseForAll(entity);
        playerSet.remove(player);
        applyDisguise(entity, disguiseString, playerSet);
    }

}
