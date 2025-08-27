package me.ppgome.critterGuard.commands;

import me.ppgome.critterGuard.CGConfig;
import me.ppgome.critterGuard.CritterGuard;
import me.ppgome.critterGuard.MessageUtil;
import me.ppgome.critterGuard.database.MountAccess;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

public class AccessSubCommand implements SubCommandHandler {

    private final CritterGuard plugin;
    private CGConfig config;

    /**
     * Constructor for CritterAccessCommand.
     * Initializes the command with the plugin instance.
     *
     * @param plugin The instance of the CritterGuard plugin.
     */
    public AccessSubCommand(CritterGuard plugin) {
        this.plugin = plugin;
        this.config = plugin.getCGConfig();
    }

    // /critter access <add/remove> <full/passenger> <playername>
    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player player)) return;

        String playerName = args[2];

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            OfflinePlayer playerBeingAdded = Bukkit.getOfflinePlayer(playerName);

            // Check if player exists (has played before)
            if (!playerBeingAdded.hasPlayedBefore() && !playerBeingAdded.isOnline()) {
                Bukkit.getScheduler().runTask(plugin, () -> player.sendMessage(MessageUtil.failedMessage(config.PREFIX,
                        "Player " + playerName + " does not exist.")));
                return;
            }

            // Parse command arguments
            boolean isAdd = args[0].equalsIgnoreCase("add");
            boolean isRemove = args[0].equalsIgnoreCase("remove");
            boolean isFullAccess = args[1].equalsIgnoreCase("full");
            boolean isPassengerAccess = args[1].equalsIgnoreCase("passenger");

            // Validate arguments
            if (!isAdd && !isRemove) {
                Bukkit.getScheduler().runTask(plugin, () -> player.sendMessage(MessageUtil.failedMessage(config.PREFIX,
                        "Invalid action. Use 'add' or 'remove'.")));
                return;
            }

            if (!isFullAccess && !isPassengerAccess) {
                Bukkit.getScheduler().runTask(plugin, () -> player.sendMessage(MessageUtil.failedMessage(config.PREFIX,
                        "Invalid access type. Use 'full' or 'passenger'.")));
                return;
            }

            // Create MountAccess object
            MountAccess mountAccess = new MountAccess(
                    playerBeingAdded.getUniqueId().toString(),
                    isFullAccess,
                    isAdd
            );

            Bukkit.getScheduler().runTask(plugin, () -> {
                UUID senderUuid = player.getUniqueId();
                plugin.getCritterCache().addAwaitingAccess(senderUuid, mountAccess);
                player.sendMessage(MessageUtil.warningMessage(config.PREFIX,
                        " Right-click the critter to "
                                + (isAdd ? "grant" : "revoke") + " "
                                + (isFullAccess ? "full" : "passenger") + " access to "
                                + playerBeingAdded.getName() + "."));

                // Set timeout
                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    if (plugin.getCritterCache().isAwaitingAccess(senderUuid)) {
                        plugin.getCritterCache().removeAwaitingAccess(senderUuid);
                        if (player.isOnline()) {
                            player.sendMessage(MessageUtil.failedMessage(config.PREFIX,
                                    "Access request timed out for " + playerBeingAdded.getName() + "."));
                        }
                    }
                }, 20L * 15L); // 15 seconds timeout
            });
        });
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        if(args.length == 1) return List.of("add", "remove");
        else if(args.length == 2) return List.of("full", "passenger");
        return null;
    }

    @Override
    public String getCommandName() {
        return "access";
    }

    @Override
    public String getDescription() {
        return "Manage access permissions for critters. Use 'add' to grant access or 'remove' to revoke access.";
    }

    @Override
    public String getUsage() {
        return "Usage: /critter access <add/remove> <full/passenger> <playername>";
    }

    @Override
    public String getPermission() {
        return "critterguard.access";
    }

    @Override
    public int getMinArgs() {
        return 3;
    }
}
