package me.ppgome.critterGuard.commands;

import me.ppgome.critterGuard.CGConfig;
import me.ppgome.critterGuard.CritterGuard;
import me.ppgome.critterGuard.MessageUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class CritterCommand implements CommandExecutor, TabCompleter {

    private CGConfig config;

    private final Map<String, SubCommandHandler> subCommands = new HashMap<>();

    //------------------------------------------------------------------------------------------------------------------

    public CritterCommand(CritterGuard plugin) {
        this.config = plugin.getCGConfig();
        registerSubCommand(new AccessSubCommand(plugin));
        registerSubCommand(new ListSubCommand(plugin));
    }

    //------------------------------------------------------------------------------------------------------------------

    protected void registerSubCommand(SubCommandHandler subCommandHandler) {
        subCommands.put(subCommandHandler.getCommandName(), subCommandHandler);
    }


    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] args) {
        if (args.length == 0) {
            sender.sendMessage(MessageUtil.failedMessage(config.PREFIX, "Usage: /critter <subcommand> [args]"));
            return true;
        }

        String subCommandName = args[0].toLowerCase();
        SubCommandHandler subCommandHandler = subCommands.get(subCommandName);

        if (subCommandHandler == null) {
            sender.sendMessage("Unknown subcommand: " + subCommandName);
            return true;
        }

        if(!sender.hasPermission(subCommandHandler.getPermission())) {
            sender.sendMessage(MessageUtil.failedMessage(config.PREFIX, "You do not have permission" +
                    " to use this command."));
            return true;
        }

        if (args.length < subCommandHandler.getMinArgs()) {
            sender.sendMessage(MessageUtil.failedMessage(config.PREFIX, subCommandHandler.getUsage()));
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
