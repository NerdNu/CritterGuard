package me.ppgome.critterGuard.commands.tpcommands;

import me.ppgome.critterGuard.*;
import me.ppgome.critterGuard.commands.SubCommandHandler;
import me.ppgome.critterGuard.utility.MessageUtils;
import me.ppgome.critterGuard.utility.PlaceholderParser;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * This class represents the command used to teleport a player's critter to you.
 */
public class TPHereSubCommand implements SubCommandHandler {

    /**
     * The instance of the plugin.
     */
    private final CritterGuard plugin;
    /**
     * The instance of the configuration class.
     */
    private static CGConfig config;

    /**
     * Constructor for an instance of the tphere subcommand.
     * @param plugin the instance of the plugin
     */
    public TPHereSubCommand(CritterGuard plugin) {
        this.plugin = plugin;
        config = plugin.getCGConfig();
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if(!(sender instanceof Player player)) return;
        TeleportUtils.validateAndExecuteTeleport(player, args[0], args[1].toLowerCase(), plugin,
                TPHereSubCommand::teleportCritterToPlayer);
    }

    /**
     * Teleports the critter to the player who ran the command.
     * @param player the player running the command
     * @param critterEntity the entity being teleported
     * @param targetPlayerName the name of the player whose critter is being teleported
     * @param plugin the instance of the plugin
     */
    private static void teleportCritterToPlayer(Player player, Entity critterEntity, String targetPlayerName, CritterGuard plugin) {
        critterEntity.teleport(player.getLocation());
        player.sendMessage(PlaceholderParser.of(config.TELEPORT_HERE).player(targetPlayerName).parse());
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        if(args.length == 1) {
            return null; // Let Bukkit handle player name completions
        }
        return List.of();
    }

    @Override
    public String getCommandName() {
        return "tphere";
    }

    @Override
    public String getDescription() {
        return "Teleport a critter to your location.";
    }

    @Override
    public Component getUsage() {
        return MessageUtils.miniMessageDeserialize(config.PREFIX + " " + getStringUsage());
    }

    @Override
    public String getStringUsage() {
        return "<red>Usage: /critter tphere <playerName> <critterName|critterUUID|index></red>";
    }

    @Override
    public String getPermission() {
        return "critterguard.tphere";
    }

    @Override
    public int getMinArgs() {
        return 2;
    }
}
