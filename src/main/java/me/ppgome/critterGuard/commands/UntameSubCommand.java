package me.ppgome.critterGuard.commands;

import me.ppgome.critterGuard.CGConfig;
import me.ppgome.critterGuard.CritterCache;
import me.ppgome.critterGuard.CritterGuard;
import me.ppgome.critterGuard.utility.MessageUtils;
import me.ppgome.critterGuard.utility.PlaceholderParser;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

public class UntameSubCommand implements SubCommandHandler {

    private final CritterGuard plugin;
    private CGConfig config;
    private CritterCache critterCache;

    public UntameSubCommand(CritterGuard plugin) {
        this.plugin = plugin;
        this.config = plugin.getCGConfig();
        this.critterCache = plugin.getCritterCache();
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if(!(sender instanceof Player player)) return;
        UUID senderUuid = player.getUniqueId();
        critterCache.addAwaitingUntame(senderUuid);
        player.sendMessage(PlaceholderParser
                .of(config.CLICK_UNTAME)
                .click()
                .parse());

        // Set timeout
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            if (critterCache.isAwaitingUntame(senderUuid)) {
                critterCache.removeAwaitingUntame(senderUuid);
                if (player.isOnline()) {
                    player.sendMessage(config.CLICK_TIMEOUT);
                }
            }
        }, 20L * 15L); // 15 seconds timeout
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        return List.of();
    }

    @Override
    public String getCommandName() {
        return "untame";
    }

    @Override
    public String getDescription() {
        return "Untame a critter.";
    }

    @Override
    public Component getUsage() {
        return MessageUtils.miniMessageDeserialize(config.PREFIX + " <red>Usage: /critter untame</red>");
    }

    @Override
    public String getPermission() {
        return "critterguard.untame";
    }

    @Override
    public int getMinArgs() {
        return 0;
    }
}