package me.ppgome.critterGuard.commands;

import io.papermc.paper.entity.LookAnchor;
import me.ppgome.critterGuard.*;
import me.ppgome.critterGuard.utility.MessageUtils;
import me.ppgome.critterGuard.utility.PlaceholderParser;
import net.kyori.adventure.text.Component;
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
            player.sendMessage(config.GPS_NO_PLAYERMETA);
            return;
        }

        // Search for the critter by name, UUID, or index
        Entity matchedEntity = CommandUtils.searchByIdentifier(critterIdentifier, playerMeta, plugin);

        // If match found, notify the player
        if(matchedEntity != null) {
            Location location = matchedEntity.getLocation();
            player.sendMessage(MessageUtils.locationBuilder(location, NamedTextColor.GREEN));
            player.lookAt(matchedEntity, LookAnchor.EYES, LookAnchor.FEET);
        } else {
            player.sendMessage(PlaceholderParser.of(config.GPS_NO_MATCH).identifier(critterIdentifier).parse());
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
    public Component getUsage() {
        return MessageUtils.miniMessageDeserialize(config.PREFIX + " <red>Usage: /critter gps <critterName OR uuid OR number>\n" +
                "Note: Partial matches for names and UUIDs are supported.</red>");
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
