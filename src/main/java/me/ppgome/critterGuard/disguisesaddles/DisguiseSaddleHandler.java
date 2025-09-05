package me.ppgome.critterGuard.disguisesaddles;

import me.ppgome.critterGuard.CGConfig;
import me.ppgome.critterGuard.CritterGuard;
import me.ppgome.critterGuard.utility.PlaceholderParser;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashSet;
import java.util.Set;

/**
 * This class provides the necessary methods for disguise saddles.
 *
 * A good portion of the code here was borrowed from EasyRider.
 */
public class DisguiseSaddleHandler {

    /**
     * The instance of the plugin.
     */
    private CritterGuard plugin;
    /**
     * The instance of the configuration class.
     */
    private CGConfig config;
    /**
     * The instance of the LibsDisguiseProvider for interacting directly with the LibsDisguises API.
     */
    private LibsDisguiseProvider disguiseProvider;
    /**
     * The prefix that's checked for in the lore of saddles and harnesses.
     */
    private final String disguisePrefix = "Disguise:";

    //------------------------------------------------------------------------------------------------------------------

    /**
     * Initializes the methods used to handle disguise saddles.
     *
     * @param plugin The instance of the plugin.
     */
    public DisguiseSaddleHandler(CritterGuard plugin) {
        this.plugin = plugin;
        this.config = plugin.getCGConfig();
        this.disguiseProvider = plugin.getDisguiseProvider();
    }

    //------------------------------------------------------------------------------------------------------------------

    /**
     * Grabs the saddle or harness from a mount, if they're wearing one.
     *
     * @param mount The mount being grabbed from
     * @return The saddle or harness
     */
    public ItemStack getSaddleFromMount(LivingEntity mount) {
        if (mount instanceof Llama) return null;

        EntityEquipment equipment = mount.getEquipment();
        if (equipment == null) return null;

        ItemStack saddle;
        if(mount instanceof HappyGhast) saddle = equipment.getItem(EquipmentSlot.BODY);
        else saddle = equipment.getItem(EquipmentSlot.SADDLE);

        Material saddleType = saddle.getType();
        return (saddleType.equals(Material.SADDLE) || isHarness(saddleType) ? saddle : null);
    }

    /**
     * Checks if the provided material is a happy ghast harness.
     *
     * This is necessary because there's 16 different kinds of harnesses, so checking their names is the easiest.
     *
     * @param harness The material being checked
     * @return True if it is a harness, false if not.
     */
    private boolean isHarness(Material harness) {
        return harness.name().endsWith("HARNESS");
    }

    /**
     * Fetches the disguise string from the mount's saddle.
     *
     * @param mount The mount whose saddle is being checked
     * @return The disguise string
     */
    public String getDisguiseFromSaddle(Entity mount) {
        ItemStack saddle = getSaddleFromMount((LivingEntity) mount);
        if (saddle == null) return null;

        ItemMeta itemMeta = saddle.getItemMeta();
        if (itemMeta != null && itemMeta.hasLore()) {
            for (Component lore : itemMeta.lore()) {
                String loreString = PlainTextComponentSerializer.plainText().serialize(lore);
                if (loreString.startsWith(disguisePrefix)) {
                    return loreString.substring(disguisePrefix.length()).trim();
                }
            }
        }
        return null;
    }

    /**
     * Applies a disguise to a mount if it's valid.
     *
     * @param mount The mount being disguised
     * @param player The player controlling the mount
     * @param disguiseType The disguise string stored in the saddle
     */
    public void applySaddleDisguise(Entity mount, Player player, String disguiseType) {
        if (disguiseType == null || disguiseProvider == null) return;

        Set<Player> playerSet = new HashSet<>(Bukkit.getOnlinePlayers());
        playerSet.remove(player);
        boolean validDisguise = disguiseProvider.applyDisguise(mount, disguiseType, playerSet);

        if (validDisguise) {
            player.sendMessage(PlaceholderParser.of(config.DISGUISE_SUCCESS).mount(disguiseType).parse());
        } else {
            plugin.logError("Mount " + mount.getUniqueId() + " accessed by " + player.getName() +
                    " has a saddle with an unsupported disguise: " + disguiseType);
        }
    }

    /**
     * Toggles whether the player controlling the mount can see its disguise or not.
     *
     * @param mount The mount that will be disguised or undisguised
     * @param player The player toggling their status
     * @param enabling True if they're enabling seeing their own disguise, false if undisguising.
     * @return True if it succeeded, false if not.
     */
    public boolean toggleSaddleDisguise(Entity mount, Player player, boolean enabling) {
        String disguiseType = getDisguiseFromSaddle(mount);
        if (disguiseType == null || plugin.getDisguiseProvider() == null) return false;

        Set<Player> playerSet = new HashSet<>(Bukkit.getOnlinePlayers());

        if(enabling) {
            return disguiseProvider.applyDisguise(mount, disguiseType, playerSet);
        } else {
            disguiseProvider.removeDisguiseForOne(mount, player, disguiseType, playerSet);
            return true;
        }
    }

    /**
     * Refreshes all active disguises.
     */
    public void refreshSaddleDisguises() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.getVehicle() instanceof Entity entity) {
                String disguiseType = getDisguiseFromSaddle(entity);
                if (disguiseType != null) {
                    applySaddleDisguise(entity, player, disguiseType);
                }
            }
        }
    }

}
