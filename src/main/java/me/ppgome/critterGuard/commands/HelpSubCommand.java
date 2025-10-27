package me.ppgome.critterGuard.commands;

import me.ppgome.critterGuard.CGConfig;
import me.ppgome.critterGuard.CritterCache;
import me.ppgome.critterGuard.CritterGuard;
import me.ppgome.critterGuard.utility.MessageUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;

public class HelpSubCommand implements SubCommandHandler {

    /**
     * The instance of the plugin.
     */
    private final CritterGuard plugin;
    /**
     * The instance of the configuration class.
     */
    private CGConfig config;
    /**
     * The instance of the plugin's cache.
     */
    private CritterCache critterCache;

    /**
     * Constructor for AccessSubCommand.
     * Initializes the command with the plugin instance.
     *
     * @param plugin The instance of the CritterGuard plugin.
     */
    public HelpSubCommand(CritterGuard plugin) {
        this.plugin = plugin;
        this.config = plugin.getCGConfig();
        this.critterCache = plugin.getCritterCache();
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if(!(sender instanceof Player player)) return;
        if(args.length > 0) {
            player.sendMessage(getUsage());
            return;
        }

        Component message = Component.text("----==== ", NamedTextColor.GRAY)
                .append(Component.text("Critter Help", NamedTextColor.GOLD))
                .append(Component.text(" ====----", NamedTextColor.GRAY))
                .appendNewline().appendNewline()
                .append(Component.text("Root commands:", NamedTextColor.GOLD)).appendNewline().appendNewline()
                .append(Component.text("/critter ", NamedTextColor.GREEN))
                    .append(Component.text("<subcommand>", NamedTextColor.YELLOW)).appendNewline()
                .append(Component.text("/cg ", NamedTextColor.GREEN))
                    .append(Component.text("<subcommand>", NamedTextColor.YELLOW))
                .appendNewline().appendNewline()
                .append(Component.text("Subcommands:", NamedTextColor.GOLD)).appendNewline().appendNewline();

        Map<String, SubCommandHandler> subCommandMap = plugin.getCritterCommand().getSubCommands();

        // Build messages sections for subcommands.
        for(SubCommandHandler subCommand : subCommandMap.values()) {
            if(!player.hasPermission(subCommand.getPermission())) continue;
            message = message.append(Component.text(subCommand.getCommandName(), NamedTextColor.GREEN))
                        .append(Component.text(" - ", NamedTextColor.GOLD))
                        .append(Component.text(subCommand.getDescription(), NamedTextColor.YELLOW)).appendNewline()
                    .append(MessageUtils.miniMessageDeserialize(subCommand.getStringUsage()))
                    .appendNewline().appendNewline();
        }

        message = message.append(Component.text("----==== ", NamedTextColor.GRAY))
                .append(Component.text("Critter Help", NamedTextColor.GOLD))
                .append(Component.text(" ====----", NamedTextColor.GRAY));

        player.sendMessage(message);
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        return List.of();
    }

    @Override
    public String getCommandName() {
        return "help";
    }

    @Override
    public String getDescription() {
        return "Lists this list of commands.";
    }

    @Override
    public Component getUsage() {
        return MessageUtils.miniMessageDeserialize(config.PREFIX + getStringUsage());
    }

    @Override
    public String getStringUsage() {
        return "<red>Usage: /critter help</red>";
    }

    @Override
    public String getPermission() {
        return "critterguard.help";
    }

    @Override
    public int getMinArgs() {
        return 0;
    }
}
