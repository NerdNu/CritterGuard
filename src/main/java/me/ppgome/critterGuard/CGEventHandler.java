package me.ppgome.critterGuard;

import me.ppgome.critterGuard.database.SavedMount;
import me.ppgome.critterGuard.database.SavedPet;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityMountEvent;
import org.bukkit.event.entity.EntityTameEvent;

import java.util.UUID;

/**
 * Handles events related to mount management.
 * This class will contain methods to handle various events such as mount spawning,
 * player interactions with mounts, and any other relevant events.
 */
public class CGEventHandler implements Listener {

    private CritterGuard plugin;
    private CritterCache critterCache;

    //------------------------------------------------------------------------------------------------------------------

    public CGEventHandler(CritterGuard plugin) {
        this.plugin = plugin;
        this.critterCache = plugin.getCritterCache();
    }

    //------------------------------------------------------------------------------------------------------------------

    @EventHandler
    public void onCritterTame(EntityTameEvent event) {
        Entity entity = event.getEntity();
        AnimalTamer tamer = event.getOwner();
        if(entity instanceof Horse horse) {
            tameHorse(horse, tamer);
        } else if(entity instanceof Llama llama) {
            tameLlama(llama, tamer);
        } else if(entity instanceof AbstractHorse || entity instanceof HappyGhast) {
            tameOtherMount(entity, tamer);
        } else {
            tamePet(entity, tamer);
        }
    }

    private void tameHorse(Horse horse, AnimalTamer tamer) {
        // Implement logic to handle taming of AbstractHorse
        // For example, you might want to set the owner or perform other actions
        // when a player tames a horse.
        if(tamer instanceof Player player) {
            SavedMount newMount = new SavedMount(horse.getUniqueId().toString(), horse.getName(),
                    tamer.getUniqueId().toString(), tamer.getName(), horse.getType().toString(),
                    horse.getColor().toString(), horse.getStyle().toString());
            plugin.registerNewSavedMount(newMount);
            player.sendMessage(Component.text("You have tamed a " + horse.getType() + "!", NamedTextColor.GREEN));
        }
    }

    private void tameLlama(Llama llama, AnimalTamer tamer) {
        // Implement logic to handle taming of Llama
        // This could involve setting the owner or performing other actions
        // when a player tames a Llama.
        if(tamer instanceof Player player) {
            SavedMount newMount = new SavedMount(llama.getUniqueId().toString(), llama.getName(),
                    tamer.getUniqueId().toString(), tamer.getName(), llama.getColor().toString());
            plugin.registerNewSavedMount(newMount);
            player.sendMessage(Component.text("You have tamed a Llama!", NamedTextColor.GREEN));
        }
    }

    private void tameOtherMount(Entity entity, AnimalTamer tamer) {
        // Implement logic for other types of entities if needed
        if(tamer instanceof Player player) {
            SavedMount newMount = new SavedMount(entity.getUniqueId().toString(), entity.getName(),
                    tamer.getUniqueId().toString(), tamer.getName(), entity.getType().toString());
            plugin.registerNewSavedMount(newMount);
            player.sendMessage(Component.text("You have tamed a " + entity.getType() + "!", NamedTextColor.GREEN));
        }
    }

    private void tamePet(Entity entity, AnimalTamer tamer) {
        // Implement logic to handle taming of pets
        // This could involve setting the owner or performing other actions
        // when a player tames a pet.
        if(tamer instanceof Player player) {
            SavedPet savedPet = new SavedPet(entity.getUniqueId().toString(), entity.getName(),
                    tamer.getUniqueId().toString(), tamer.getName(), entity.getType().toString());
            plugin.registerNewSavedPet(savedPet);
            player.sendMessage(Component.text("You have tamed a pet: " + entity.getType() + "!", NamedTextColor.GREEN));
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
    public void onEntityMount(EntityMountEvent event) {
        Entity passenger = event.getEntity();
        if(!(passenger instanceof Player)) return; // Only handle player mounts
        Entity mount = event.getMount();
        UUID mountUuid = mount.getUniqueId();
        SavedMount savedMount = critterCache.getSavedMount(mountUuid);
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
