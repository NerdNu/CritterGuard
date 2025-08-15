package me.ppgome.critterGuard.commands;

import me.ppgome.critterGuard.CritterGuard;
import org.bukkit.command.CommandSender;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CritterCommand {

    private CritterGuard plugin;

    private final Map<String, SubCommandHandler> subCommands = new HashMap<>();

    //------------------------------------------------------------------------------------------------------------------

    public CritterCommand(CritterGuard plugin) {
        this.plugin = plugin;
        registerSubCommand(new AccessSubCommand(plugin));
    }

    //------------------------------------------------------------------------------------------------------------------

    protected void registerSubCommand(SubCommandHandler subCommandHandler) {
        subCommands.put(subCommandHandler.getCommandName(), subCommandHandler);
    }

    public boolean execute(CommandSender sender, String[] args) {
        if (args.length == 0) {
            sender.sendMessage("Usage: /critter <subcommand> [args]");
            return false;
        }

        String subCommandName = args[0].toLowerCase();
        SubCommandHandler subCommandHandler = subCommands.get(subCommandName);

        if (subCommandHandler == null) {
            sender.sendMessage("Unknown subcommand: " + subCommandName);
            return false;
        }

        if(sender.hasPermission(subCommandHandler.getPermission())) {
            sender.sendMessage("You do not have permission to use this command.");
            return false;
        }

        if (args.length < subCommandHandler.getMinArgs()) {
            sender.sendMessage(subCommandHandler.getUsage());
            return false;
        }

        return subCommandHandler.execute(sender, args);
    }

    public List<String> tabComplete(CommandSender sender, String[] args) {
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

        // More than one argument - delegate to subcommand
        if (args.length > 1) {
            String subCommandName = args[0].toLowerCase();
            SubCommandHandler subCommand = subCommands.get(subCommandName);

            if (subCommand != null) {
                String permission = subCommand.getPermission();
                if (permission == null || sender.hasPermission(permission)) {
                    String[] subArgs = Arrays.copyOfRange(args, 1, args.length);
                    return subCommand.tabComplete(sender, subArgs);
                }
            }
        }

        return List.of();
    }



}
