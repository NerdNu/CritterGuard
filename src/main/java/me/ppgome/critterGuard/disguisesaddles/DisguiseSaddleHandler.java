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

    private CritterGuard plugin;
    private CGConfig config;
    private LibsDisguiseProvider disguiseProvider;
    private final String disguisePrefix = "Disguise:";

    //------------------------------------------------------------------------------------------------------------------

    public DisguiseSaddleHandler(CritterGuard plugin) {
        this.plugin = plugin;
        this.config = plugin.getCGConfig();
        this.disguiseProvider = plugin.getDisguiseProvider();
    }

    //------------------------------------------------------------------------------------------------------------------

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

    private boolean isHarness(Material harness) {
        return harness.name().endsWith("HARNESS");
    }

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
