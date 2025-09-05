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
 * This class represents the command used to teleport to a player's critter.
 */
public class TPSubCommand implements SubCommandHandler {

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
    public TPSubCommand(CritterGuard plugin) {
        this.plugin = plugin;
        this.config = plugin.getCGConfig();
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if(!(sender instanceof Player player)) return;
        TeleportUtils.validateAndExecuteTeleport(player, args[0], args[1].toLowerCase(), plugin,
                TPSubCommand::teleportPlayerToCritter);
    }

    /**
     * Teleports the player to the critter specified in the command.
     * @param player the player running the command
     * @param critterEntity the entity being teleported to
     * @param targetPlayerName the name of the player whose critter is being teleported to
     * @param plugin the instance of the plugin
     */
    private static void teleportPlayerToCritter(Player player, Entity critterEntity, String targetPlayerName, CritterGuard plugin) {
        player.teleport(critterEntity.getLocation());
        player.sendMessage(PlaceholderParser.of(config.TELEPORT_TO).player(targetPlayerName).parse());
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
        return "tp";
    }

    @Override
    public String getDescription() {
        return "Teleport to a critter's location.";
    }

    @Override
    public Component getUsage() {
        return MessageUtils.miniMessageDeserialize(config.PREFIX +
                " <red>Usage: /critter tp <playerName> <critterName|critterUUID|index></red>");
    }

    @Override
    public String getPermission() {
        return "critterguard.tp";
    }

    @Override
    public int getMinArgs() {
        return 2;
    }
}
