package me.ppgome.critterGuard.commands;

import me.ppgome.critterGuard.CritterGuard;
import org.bukkit.command.CommandSender;

import java.util.List;

public class AccessSubCommand implements SubCommandHandler {

    private final CritterGuard plugin;

    /**
     * Constructor for CritterAccessCommand.
     * Initializes the command with the plugin instance.
     *
     * @param plugin The instance of the CritterGuard plugin.
     */
    public AccessSubCommand(CritterGuard plugin) {
        this.plugin = plugin;
    }

    // /critter access <add/remove> <playername>
    @Override
    public boolean execute(CommandSender sender, String[] args) {
        return false;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        return null;
    }

    @Override
    public String getCommandName() {
        return null;
    }

    @Override
    public String getDescription() {
        return null;
    }

    @Override
    public String getUsage() {
        return "Usage: /critter access <add/remove> <playername>";
    }

    @Override
    public String getPermission() {
        return null;
    }

    @Override
    public int getMinArgs() {
        return 3;
    }
}
