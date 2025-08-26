package me.ppgome.critterGuard.commands;

import me.ppgome.critterGuard.*;
import me.ppgome.critterGuard.database.SavedAnimal;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Animals;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wolf;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class ListSubCommand implements SubCommandHandler {

    private final CritterGuard plugin;
    private CGConfig config;
    private CritterCache critterCache;
    private List<String> entityTypes;

    /**
     * Constructor for CritterAccessCommand.
     * Initializes the command with the plugin instance.
     *
     * @param plugin The instance of the CritterGuard plugin.
     */
    public ListSubCommand(CritterGuard plugin) {
        this.plugin = plugin;
        this.config = plugin.getCGConfig();
        this. critterCache = plugin.getCritterCache();
        entityTypes = new ArrayList<>(List.of("all", "horse", "mule", "donkey", "camel", "llama",
                "happy_ghast", "wolf", "cat", "parrot"));
    }

    // /critter list [entityType] [playerName] [pageNumber]
    @Override
    public void execute(CommandSender sender, String[] args) {

        if(!(sender instanceof Player player)) return; // Ensure the command sender is a player
        boolean isEntityType;
        boolean isPageNumber;

        switch(args.length) {
            // No arguments provided. Return all critters for the player.
            case 0:
                getData(player, "all", critterCache.getPlayerMeta(player.getUniqueId()), 1);
                break;
            // One argument provided. Could be an entity type, page, or a player name.
            case 1:
                if(entityTypes.contains(args[0].toLowerCase())) {
                    getData(player, args[0].toLowerCase(), critterCache.getPlayerMeta(player.getUniqueId()), 1);
                } else if(args[0].matches("\\d+")) {
                    getData(player, "all", critterCache.getPlayerMeta(player.getUniqueId()),
                            Integer.parseInt(args[0]));
                } else {
                    getDataAsync(player, "all", getPlayerMeta(args[0]), 1);
                }
                break;
                // Two arguments provided. Could be entity type and player name, entity type and page, or player name and page.
            case 2:
                isEntityType = entityTypes.contains(args[0].toLowerCase());
                isPageNumber = args[1].matches("\\d+");
                if(isEntityType && isPageNumber) {
                    getData(player, args[0].toLowerCase(), critterCache.getPlayerMeta(player.getUniqueId()),
                            Integer.parseInt(args[1]));
                } else if(isEntityType) {
                    getDataAsync(player, args[0].toLowerCase(), getPlayerMeta(args[1]), 1);
                } else if(isPageNumber) {
                    getDataAsync(player, "all", getPlayerMeta(args[0]), Integer.parseInt(args[1]));
                }
                break;
                // Three arguments provided. Should be entity type, player name, and page number.
            case 3:
                isPageNumber = args[2].matches("\\d+");
                isEntityType = entityTypes.contains(args[0].toLowerCase());
                if(isEntityType && isPageNumber) {
                    getDataAsync(player, args[0].toLowerCase(), getPlayerMeta(args[1]), Integer.parseInt(args[2]));
                }
                break;
        }
    }

    /**
     * Retrieves player metadata and outputs the list of animals for the specified page.
     *
     * @param player      The player who executed the command.
     * @param entityType  The type of entity to filter by (e.g., "horse", "cat").
     * @param playerMeta  The PlayerMeta data for the specified player.
     * @param page        The page number to retrieve.
     */
    private void getData(Player player, String entityType, PlayerMeta playerMeta, int page) {
        if(playerMeta != null && !playerMeta.getOwnedList().isEmpty()){
                outputList(getAnimalsPerPage(playerMeta.getOwnedList(), entityType, page), player);
                return;
        }

        player.sendMessage(MessageUtil.failedMessage(config.PREFIX, "Player " + player.getName() +
                " does not exist or has no critters."));
    }

    /**
     * Asynchronously retrieves player metadata and outputs the list of animals for the specified page.
     *
     * @param player      The player who executed the command.
     * @param entityType  The type of entity to filter by (e.g., "horse", "cat").
     * @param playerMeta  A CompletableFuture containing the PlayerMeta data.
     * @param page        The page number to retrieve.
     */
    private void getDataAsync(Player player, String entityType, CompletableFuture<PlayerMeta> playerMeta, int page) {
        playerMeta.thenAccept(meta -> Bukkit.getScheduler().runTask(plugin, () -> {
            if (meta != null && !meta.getOwnedList().isEmpty()) {
                outputList(getAnimalsPerPage(meta.getOwnedList(), entityType, page), player);
            } else {
                player.sendMessage(MessageUtil.failedMessage(config.PREFIX, "That player doesn't exist" +
                        " or has no critters."));
            }
        }));
    }

    /**
     * Retrieves a sublist of animals for the specified page.
     * Each page contains a maximum of 5 animals.
     *
     * @param animalList The list of all animals.
     * @param page       The page number to retrieve.
     * @return A sublist of animals for the specified page.
     */
    private List<SavedAnimal> getAnimalsPerPage(List<SavedAnimal> animalList, String entityType, int page) {

        System.out.println(animalList);

        List<SavedAnimal> filteredList = new ArrayList<>();

        if (entityType.equals("all")) {
            filteredList.addAll(animalList); // If "all", return the entire list
        } else {
            for (SavedAnimal animal : animalList) {
                if (animal.getEntityType().equalsIgnoreCase(entityType)) {
                    filteredList.add(animal); // Filter by entity type
                }
            }
        }

        int totalPages = (int) Math.ceil((double) animalList.size() / 5); // Calculate total pages based on 5 items per page
        if (page > totalPages) page = totalPages; // Ensure page does not exceed total pages

        int startIndex = (page - 1) * 5; // Calculate start index for the page
        int endIndex = Math.min(startIndex + 5, animalList.size()); // Calculate end index, ensuring it does not exceed the list size
        return filteredList.subList(startIndex, endIndex); // Return the sublist for the current page
    }

    /**
     * Outputs the list of animals to the player.
     *
     * @param animalList The list of animals to output.
     * @param player     The player who executed the command.
     */
    private void outputList(List<SavedAnimal> animalList, Player player) {
        if (animalList.isEmpty()) {
            player.sendMessage(MessageUtil.failedMessage(config.PREFIX, "No critters found for the specified criteria."));
            return;
        }
        Component message = Component.text("----==== ", NamedTextColor.GRAY)
                .append(Component.text("Critter List", NamedTextColor.GOLD))
                .append(Component.text(" ====----", NamedTextColor.GRAY))
                .appendNewline();
        int index = 1;
        for (SavedAnimal animal : animalList) {
            String Uuid = animal.getEntityUuid().substring(0, 8);
            String name = animal.getEntityName() != null ? animal.getEntityName() : "No name";
            String type = animal.getEntityType();
            String color = animal.getColor() != null ? animal.getColor() : "N/A";
            Location location = Bukkit.getEntity(UUID.fromString(animal.getEntityUuid())).getLocation();
            message = message.appendNewline()
                    .append(Component.text("[" + index + "] ", NamedTextColor.GOLD))
                    .append(Component.text(Uuid + "... ", NamedTextColor.GRAY))
                    .append(Component.text(name, NamedTextColor.GREEN)).appendNewline()
                    .append(Component.text("     Type: " + type, NamedTextColor.BLUE)).appendNewline()
                    .append(Component.text("     Color: " + MessageUtil.capitalizeFirstLetter(color),
                            NamedTextColor.YELLOW)).appendNewline()
                    .append(Component.text("     Location: " + location.getBlockX()
                            + ", " + location.getBlockY()
                            + ", " + location.getBlockZ()
                                    + " in world: " + location.getWorld().getName(), NamedTextColor.RED));
        }
        player.sendMessage(message);
    }

    private CompletableFuture<PlayerMeta> getPlayerMeta(String playerName) {
        return CompletableFuture.supplyAsync(() -> {
            OfflinePlayer player = Bukkit.getOfflinePlayer(playerName);
            if (!player.hasPlayedBefore()) {
                return null; // Player does not exist
            }
            return critterCache.getPlayerMeta(player.getUniqueId());
        });
    }
    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        return List.of();
    }

    @Override
    public String getCommandName() {
        return "list";
    }

    @Override
    public String getDescription() {
        return "Display a list of critters for a specific entity type and player.";
    }

    @Override
    public String getUsage() {
        return "Usage: /critter list [entityType] [playerName] [pageNumber]";
    }

    @Override
    public String getPermission() {
        return "";
    }

    @Override
    public int getMinArgs() {
        return 1;
    }
}
