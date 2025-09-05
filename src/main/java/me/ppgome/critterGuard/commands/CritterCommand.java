package me.ppgome.critterGuard.commands;

import me.ppgome.critterGuard.CGConfig;
import me.ppgome.critterGuard.CritterGuard;
import me.ppgome.critterGuard.utility.MessageUtils;
import me.ppgome.critterGuard.commands.tpcommands.TPHereSubCommand;
import me.ppgome.critterGuard.commands.tpcommands.TPSubCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * This class handles all Bukkit command interaction and registers all of CritterGuard's subcommands.
 */
public class CritterCommand implements CommandExecutor, TabCompleter {

    /**
     * The instance of the configuration class.
     */
    private CGConfig config;
    /**
     * The map of CritterGuard's subcommands. Stored as name to SubCommandHandler instance.
     */
    private final Map<String, SubCommandHandler> subCommands = new HashMap<>();

    //------------------------------------------------------------------------------------------------------------------

    /**
     * Initializes the plugin's command handler and registers all subcommands.
     *
     * @param plugin The instance of the plugin.
     */
    public CritterCommand(CritterGuard plugin) {
        this.config = plugin.getCGConfig();
        registerSubCommand(new AccessSubCommand(plugin));
        registerSubCommand(new ListSubCommand(plugin));
        registerSubCommand(new GPSSubCommand(plugin));
        registerSubCommand(new TPSubCommand(plugin));
        registerSubCommand(new TPHereSubCommand(plugin));
        registerSubCommand(new TameSubCommand(plugin));
        registerSubCommand(new UntameSubCommand(plugin));
        registerSubCommand(new ReloadSubCommand(plugin));
        registerSubCommand(new ToggleNotifsSubCommand(plugin));
        registerSubCommand(new ShowDisguiseSubCommand(plugin));
    }

    //------------------------------------------------------------------------------------------------------------------

    /**
     * Stores the specified subcommand in the map.
     *
     * @param subCommandHandler The subcommand instance
     */
    protected void registerSubCommand(SubCommandHandler subCommandHandler) {
        subCommands.put(subCommandHandler.getCommandName(), subCommandHandler);
    }


    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] args) {
        if (args.length == 0) {
            sender.sendMessage(MessageUtils.miniMessageDeserialize(config.PREFIX
                    + " <red>Usage: /critter <subcommand> [args]</red>"));
            return true;
        }

        String subCommandName = args[0].toLowerCase();
        SubCommandHandler subCommandHandler = subCommands.get(subCommandName);

        if (subCommandHandler == null) {
            sender.sendMessage(MessageUtils.miniMessageDeserialize(config.PREFIX + " <red>Unknown subcommand: " +
                subCommandName + "</red>"));
            return true;
        }

        if(!sender.hasPermission(subCommandHandler.getPermission())) {
            sender.sendMessage(config.PERMISSION_COMMAND);
            return true;
        }

        if (args.length < subCommandHandler.getMinArgs() + 1) {
            sender.sendMessage(subCommandHandler.getUsage());
            return true;
        }
        subCommandHandler.execute(sender, Arrays.copyOfRange(args, 1, args.length));
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] args) {
        if (args.length == 1) {
            return subCommands.keySet().stream()
                    .filter(name -> {
                        SubCommandHandler sub = subCommands.get(name);
                        String permission = sub.getPermission();
                        return permission == null || sender.hasPermission(permission);
                    })
                    .filter(name -> name.toLowerCase().startsWith(args[0].toLowerCase()))
                    .toList();
        }

        if (args.length > 1) {
            String subCommandName = args[0].toLowerCase();
            SubCommandHandler subCommand = subCommands.get(subCommandName);

            if (subCommand != null) {
                String permission = subCommand.getPermission();
                if (permission == null || sender.hasPermission(permission)) {
                    return subCommand.tabComplete(sender, Arrays.copyOfRange(args, 1, args.length));
                }
            }
        }

        return new ArrayList<>();
    }
}
