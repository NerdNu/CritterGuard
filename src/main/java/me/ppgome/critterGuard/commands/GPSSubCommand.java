package me.ppgome.critterGuard.commands;

import io.papermc.paper.entity.LookAnchor;
import me.ppgome.critterGuard.*;
import me.ppgome.critterGuard.database.SavedAnimal;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;

import java.util.List;

public class GPSSubCommand implements SubCommandHandler {

    private final CritterGuard plugin;
    private CGConfig config;
    private CritterCache critterCache;

    public GPSSubCommand(CritterGuard plugin) {
        this.plugin = plugin;
        this.config = plugin.getCGConfig();
        this.critterCache = plugin.getCritterCache();
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if(!(sender instanceof org.bukkit.entity.Player player)) return;
        String critterIdentifier = args[0].toLowerCase();
        PlayerMeta playerMeta = critterCache.getPlayerMeta(player.getUniqueId());

        // Validate player metadata
        if(playerMeta == null) {
            player.sendMessage(MessageUtil.failedMessage(config.PREFIX, "Player metadata not found."));
            return;
        }

        // Search for the critter by name, UUID, or index
        Entity matchedEntity = CommandUtils.searchByIdentifier(critterIdentifier, playerMeta, plugin);

        // If match found, notify the player
        if(matchedEntity != null) {
            Location location = matchedEntity.getLocation();
            player.sendMessage(MessageUtil.locationBuilder(location, NamedTextColor.GREEN));
            player.lookAt(matchedEntity, LookAnchor.EYES, LookAnchor.FEET);
        } else {
            player.sendMessage(MessageUtil.failedMessage(config.PREFIX, "No critter found matching '" +
                    critterIdentifier + "'."));
        }
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        return List.of();
    }

    @Override
    public String getCommandName() {
        return "gps";
    }

    @Override
    public String getDescription() {
        return "Get the location of one of your critters.";
    }

    @Override
    public String getUsage() {
        return "Usage: /critter gps <critterName OR uuid OR number>";
    }

    @Override
    public String getPermission() {
        return "critterguard.gps";
    }

    @Override
    public int getMinArgs() {
        return 1;
    }
}
