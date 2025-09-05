package me.ppgome.critterGuard.commands;

import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;

import java.util.List;

/**
 * An interface that provides common methods for all subcommands.
 */
public interface SubCommandHandler {

    /**
     * Execute the command
     *
     * @param sender The command sender
     * @param args   Command arguments
     * @return true if command was handled successfully
     */
    void execute(CommandSender sender, String[] args);

    /**
     * Get tab completion suggestions
     *
     * @param sender The command sender
     * @param args   Current arguments
     * @return List of suggestions
     */
    List<String> tabComplete(CommandSender sender, String[] args);

    //------------------------------------------------------------------------------------------------------------------

    /**
     * Get the command name
     * @return Command name
     */
    String getCommandName();

    /**
     * Get command description
     * @return Command description
     */
    String getDescription();

    /**
     * Get command usage
     * @return Usage string
     */
    Component getUsage();

    /**
     * Get required permission
     * @return Permission node, or null if no permission required
     */
    String getPermission();

    /**
     * Get minimum number of arguments required for this command
     * @return Minimum number of arguments
     */
    int getMinArgs();
}