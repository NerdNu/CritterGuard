package me.ppgome.critterGuard.commands;

import me.ppgome.critterGuard.*;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;

import java.util.List;

public class TPHereSubCommand implements SubCommandHandler {

    private final CritterGuard plugin;
    private CGConfig config;
    private CritterCache critterCache;

    public TPHereSubCommand(CritterGuard plugin) {
        this.plugin = plugin;
        this.config = plugin.getCGConfig();
        this.critterCache = plugin.getCritterCache();
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if(!(sender instanceof org.bukkit.entity.Player player)) return;
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {

            String targetPlayerName = args[0];
            String critterIdentifier = args[1].toLowerCase();

            OfflinePlayer targetPlayer = Bukkit.getOfflinePlayer(targetPlayerName);

            if(!targetPlayer.hasPlayedBefore() && !targetPlayer.isOnline()) {
                Bukkit.getScheduler().runTask(plugin, () -> player.sendMessage(MessageUtil.failedMessage(config.PREFIX,
                        "Unable to find player '" + targetPlayerName + "'.")));
                return;
            }

            PlayerMeta playerMeta = critterCache.getPlayerMeta(targetPlayer.getUniqueId());

            if(playerMeta == null) {
                Bukkit.getScheduler().runTask(plugin, () -> player.sendMessage(MessageUtil.failedMessage(config.PREFIX,
                        "Player data not found.")));
                return;
            }

            Bukkit.getScheduler().runTask(plugin, () -> {
                Entity matchedEntity = CommandUtils.searchByIdentifier(critterIdentifier, playerMeta, plugin);

                if(matchedEntity != null) {
                    matchedEntity.teleport(player.getLocation());
                    player.sendMessage(MessageUtil.normalMessage(config.PREFIX,
                            "Teleported " + targetPlayerName + "'s critter to you."));
                } else {
                    player.sendMessage(MessageUtil.failedMessage(config.PREFIX, "No critter found matching '"
                            + critterIdentifier + "' for player '" + targetPlayerName + "'."));
                }

            });

        });
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
    public String getUsage() {
        return "Usage: /critter tphere <playerName> <critterName|critterUUID|index>";
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
