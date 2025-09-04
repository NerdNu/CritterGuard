package me.ppgome.critterGuard.commands;

import me.ppgome.critterGuard.CGConfig;
import me.ppgome.critterGuard.CritterGuard;
import me.ppgome.critterGuard.utility.MessageUtils;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;

import java.util.List;

public class ReloadSubCommand implements SubCommandHandler {

    private CGConfig config;

    /**
     * Constructor for ReloadSubCommand.
     * Initializes the command with the plugin instance.
     *
     * @param plugin The instance of the CritterGuard plugin.
     */
    public ReloadSubCommand(CritterGuard plugin) {
        this.config = plugin.getCGConfig();
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if(args.length == 0) {
            config.reload();
            sender.sendMessage(MessageUtils.miniMessageDeserialize(config.PREFIX + " <green>Configuration reloaded!</green>"));
        } else {
            sender.sendMessage(getUsage());
        }
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        return List.of();
    }

    @Override
    public String getCommandName() {
        return "reload";
    }

    @Override
    public String getDescription() {
        return "Reload CritterGuard's configuration file.";
    }

    @Override
    public Component getUsage() {
        return MessageUtils.miniMessageDeserialize(config.PREFIX + " <red>Usage: /critter reload</red>");
    }

    @Override
    public String getPermission() {
        return "critterguard.reload";
    }

    @Override
    public int getMinArgs() {
        return 0;
    }
}
