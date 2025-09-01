package me.ppgome.critterGuard.commands;

import me.ppgome.critterGuard.CGConfig;
import me.ppgome.critterGuard.CritterCache;
import me.ppgome.critterGuard.CritterGuard;
import me.ppgome.critterGuard.utility.MessageUtils;
import me.ppgome.critterGuard.utility.PlaceholderParser;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

import java.util.List;
import java.util.UUID;

public class TameSubCommand implements SubCommandHandler {

    private final CritterGuard plugin;
    private CGConfig config;
    private CritterCache critterCache;

    public TameSubCommand(CritterGuard plugin) {
        this.plugin = plugin;
        this.config = plugin.getCGConfig();
        this.critterCache = plugin.getCritterCache();
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if(!(sender instanceof org.bukkit.entity.Player player)) return;

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            String targetPlayerName = args[0];
            OfflinePlayer targetPlayer = Bukkit.getOfflinePlayerIfCached(targetPlayerName);

            if(targetPlayer.hasPlayedBefore()) {
                Bukkit.getScheduler().runTask(plugin, () -> {
                    UUID senderUuid = player.getUniqueId();
                    critterCache.addAwaitingTame(senderUuid, targetPlayer);
                    player.sendMessage(PlaceholderParser
                            .of(config.CLICK_TAME)
                            .player(targetPlayer.getName())
                            .click()
                            .parse());

                    // Set timeout
                    Bukkit.getScheduler().runTaskLater(plugin, () -> {
                        if (critterCache.isAwaitingTame(senderUuid)) {
                            critterCache.removeAwaitingTame(senderUuid);
                            if (player.isOnline()) {
                                player.sendMessage(config.CLICK_TIMEOUT);
                            }
                        }
                    }, 20L * 15L); // 15 seconds timeout
                });
            }
        });

    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        return null; // Let Bukkit handle player name completions
    }

    @Override
    public String getCommandName() {
        return "tame";
    }

    @Override
    public String getDescription() {
        return "Forces an entity to be tamed to the specified player.";
    }

    @Override
    public Component getUsage() {
        return MessageUtils.miniMessageDeserialize(config.PREFIX + " <red>Usage: /critter tame <playerName></red>");
    }

    @Override
    public String getPermission() {
        return "critterguard.tame";
    }

    @Override
    public int getMinArgs() {
        return 1;
    }
}
