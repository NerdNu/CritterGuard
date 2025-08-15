package me.ppgome.mountGuard;

import me.ppgome.mountGuard.database.SavedMount;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDismountEvent;
import org.bukkit.event.entity.EntityMountEvent;
import org.bukkit.event.entity.EntityTameEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Handles events related to mount management.
 * This class will contain methods to handle various events such as mount spawning,
 * player interactions with mounts, and any other relevant events.
 */
public class MGEventHandler implements Listener {

    private MountGuard plugin;

    //------------------------------------------------------------------------------------------------------------------

    public MGEventHandler(MountGuard plugin) {
        this.plugin = plugin;
    }

    //------------------------------------------------------------------------------------------------------------------

    @EventHandler
    public void onMountTame(EntityTameEvent event) {
        Entity entity = event.getEntity();
        AnimalTamer tamer = event.getOwner();
        if(entity instanceof Horse horse) {
            tameHorse(horse, tamer);
        } else if(entity instanceof Llama llama) {
            tameLlama(llama, tamer);
        } else if(entity instanceof AbstractHorse || entity instanceof HappyGhast) {
            tameOther(entity, tamer);
        }
    }

    private void tameHorse(Horse horse, AnimalTamer tamer) {
        // Implement logic to handle taming of AbstractHorse
        // For example, you might want to set the owner or perform other actions
        // when a player tames a horse.
        SavedMount newMount = new SavedMount(horse.getUniqueId().toString(), horse.getName(),
                tamer.getUniqueId().toString(), tamer.getName(), horse.getType().toString(),
                horse.getColor().toString(), horse.getStyle().toString());
        plugin.registerNewSavedMount(newMount);
        if(tamer instanceof Player player) {
            player.sendMessage(Component.text("You have tamed a " + horse.getType() + "!", NamedTextColor.GREEN));
        }
    }

    private void tameLlama(Llama llama, AnimalTamer tamer) {
        // Implement logic to handle taming of Llama
        // This could involve setting the owner or performing other actions
        // when a player tames a Llama.
        SavedMount newMount = new SavedMount();
    }

    private void tameOther(Entity entity, AnimalTamer tamer) {
        // Implement logic for other types of entities if needed
        SavedMount newMount = new SavedMount();
    }

    @EventHandler
    public void onEntityMount(EntityMountEvent event) {
        Entity passenger = event.getEntity();
        if(!(passenger instanceof Player)) return; // Only handle player mounts
        Entity mount = event.getMount();
        UUID mountUuid = mount.getUniqueId();
        SavedMount savedMount = plugin.getSavedMount(mountUuid);
        if(savedMount != null) {
            if(mount.getPassengers().size() > 1) {
                if(savedMount.hasAccess(passenger.getUniqueId())) return; // Player already has access to this mount
            } else if(savedMount.hasFullAccess(passenger.getUniqueId())) return;
            else {
                // Player does not have access, prevent mounting
                event.setCancelled(true);
                passenger.sendMessage(Component.text("You do not have permission to mount this entity.", NamedTextColor.RED));
            }
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
