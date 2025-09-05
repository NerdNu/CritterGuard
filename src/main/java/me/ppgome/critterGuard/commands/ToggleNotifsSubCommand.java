package me.ppgome.critterGuard.commands;

import me.ppgome.critterGuard.CGConfig;
import me.ppgome.critterGuard.CritterCache;
import me.ppgome.critterGuard.CritterGuard;
import me.ppgome.critterGuard.PlayerMeta;
import me.ppgome.critterGuard.utility.MessageUtils;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * This class represents the command used to toggle owner notifications.
 */
public class ToggleNotifsSubCommand implements SubCommandHandler {

    /**
     * The instance of the configuration class.
     */
    private CGConfig config;
    /**
     * The instance of the plugin's cache.
     */
    private CritterCache critterCache;

    /**
     * Constructor for ToggleNotifsSubCommand.
     * Initializes the command with the plugin instance.
     *
     * @param plugin The instance of the CritterGuard plugin.
     */
    public ToggleNotifsSubCommand(CritterGuard plugin) {
        this.config = plugin.getCGConfig();
        this.critterCache = plugin.getCritterCache();
    }

    // /critter notifications [on/off]
    @Override
    public void execute(CommandSender sender, String[] args) {
        if(sender instanceof Player player) {
            PlayerMeta playerMeta = critterCache.getPlayerMeta(player.getUniqueId());
            boolean isEnabled = playerMeta.showNotifications();
            int argsLength = args.length;

            if(argsLength == 0) playerMeta.toggleNotifications(!isEnabled);

            else if(argsLength == 1) {
                String param = args[0];
                if(param.equalsIgnoreCase("on")) {
                    if(isEnabled) player.sendMessage(config.NOTIFICATION_ALREADY_ON);
                    else playerMeta.toggleNotifications(true);
                } else if(param.equalsIgnoreCase("off")) {
                    if(!isEnabled) player.sendMessage(config.NOTIFICATION_ALREADY_OFF);
                    else playerMeta.toggleNotifications(false);
                } else {
                    player.sendMessage(getUsage());
                }
            } else {
                player.sendMessage(getUsage());
            }
        }
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        if(args.length == 1) {
            return List.of("on", "off");
        }
        return null;
    }

    @Override
    public String getCommandName() {
        return "notifications";
    }

    @Override
    public String getDescription() {
        return "Mount/dismount/death notifications for your noble steeds and related critters.";
    }

    @Override
    public Component getUsage() {
        return MessageUtils.miniMessageDeserialize(config.PREFIX + " <red>Usage: /critter notifications [on/off]</red>");
    }

    @Override
    public String getPermission() {
        return "critterguard.notifications";
    }

    @Override
    public int getMinArgs() {
        return 0;
    }
}
