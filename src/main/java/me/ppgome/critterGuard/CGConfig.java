package me.ppgome.critterGuard;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class CGConfig {

    private CritterGuard plugin;
    private MiniMessage mm;

    // Others
    public String PREFIX;

    // Toggles
    public boolean CAN_BREED_LOCKED_ANIMALS = false;

    // Messages
    /*
    Note: Values that support placeholders (playernames, buttons, other Strings) should be saved as String.
        Otherwise, store them as Component and deserialize them through MiniMessage right away.

    Placeholders need to be replaced at request and cannot be set here. Use the methods in MessageUtils to deserialize.
     */
    public String GRANTED_FULL_ACCESS;
    public String GRANTED_PASSENGER_ACCESS;
    public String TARGET_GRANTED_FULL_ACCESS;
    public String TARGET_GRANTED_PASSENGER_ACCESS;
    public String REVOKED_FULL_ACCESS;
    public String REVOKED_PASSENGER_ACCESS;
    public String TARGET_REVOKED_FULL_ACCESS;
    public String TARGET_REVOKED_PASSENGER_ACCESS;
    public String ACCESS_NO_PLAYER;
    public Component ALREADY_HAS_ACCESS;
    public Component ALREADY_HAS_NO_ACCESS;
    public Component DOES_NOT_SUPPORT_PASSENGERS;
    public String GPS_NO_MATCH;
    public Component GPS_NO_PLAYERMETA;
    public Component LIST_DOES_NOT_EXIST_OR_OWN;
    public Component LIST_NO_MATCH;
    public Component PERMISSION_COMMAND;
    public Component PERMISSION_INTERACT;
    public Component PERMISSION_MOUNT;
    public String SEAT_SWAP_SUCCESS;
    public Component SEAT_SWAP_FAILURE;
    public Component TAMING_TO_THEMSELVES;
    public String TAMING_TO_OTHERS;
    public Component UNTAME;
    public Component NOT_TAMED;
    public Component TAMED_NOT_YOURS;
    public String TELEPORT_TO;
    public String TELEPORT_HERE;
    public String TELEPORT_NO_PLAYER;
    public Component TELEPORT_NO_PLAYERMETA;
    public String  TELEPORT_NO_MATCH;
    public String CLICK_GRANT_FULL_ACCESS;
    public String CLICK_GRANT_PASSENGER_ACCESS;
    public String CLICK_REVOKE_FULL_ACCESS;
    public String CLICK_REVOKE_PASSENGER_ACCESS;
    public String CLICK_TAME;
    public String CLICK_UNTAME;
    public Component CLICK_TIMEOUT;

    public CGConfig(CritterGuard plugin) {
        this.plugin = plugin;
        mm = MiniMessage.miniMessage();
        plugin.saveDefaultConfig();
        plugin.reloadConfig();
        try {
            reload();
        } catch(InvalidConfigurationException exception) {
            plugin.logError("Configuration failed to load due to an exception.\n" + exception.getMessage());
        }
    }

    public void reload() throws InvalidConfigurationException {
        plugin.reloadConfig();
        FileConfiguration config = plugin.getConfig();
        String errorMessage = "<red>The config option for this message is missing. Let an admin know!</red>";

        PREFIX = config.getString("pluginPrefix", "<gold>[</gold><green>CritterGuard</green><gold>]</gold>");

        // TOGGLES
        CAN_BREED_LOCKED_ANIMALS = plugin.getConfig().getBoolean("toggles.canBreedLockedAnimals", true);

        // MESSAGES
        // --ACCESS
        String accessPath = "messages.access.";
        GRANTED_FULL_ACCESS = PREFIX
                + " " + config.getString(accessPath + "grantedFullAccess", errorMessage);
        GRANTED_PASSENGER_ACCESS = PREFIX
                + " " + config.getString(accessPath + "grantedPassengerAccess", errorMessage);
        TARGET_GRANTED_FULL_ACCESS = PREFIX
                + " " + config.getString(accessPath + "targetGrantedFullAccess", errorMessage);
        TARGET_GRANTED_PASSENGER_ACCESS = PREFIX
                + " " + config.getString(accessPath + "targetGrantedPassengerAccess", errorMessage);
        REVOKED_FULL_ACCESS = PREFIX
                + " " + config.getString(accessPath + "revokedFullAccess", errorMessage);
        REVOKED_PASSENGER_ACCESS = PREFIX
                + " " + config.getString(accessPath + "revokedPassengerAccess", errorMessage);
        TARGET_REVOKED_FULL_ACCESS = PREFIX
                + " " + config.getString(accessPath + "targetRevokedFullAccess", errorMessage);
        TARGET_REVOKED_PASSENGER_ACCESS = PREFIX
                + " " + config.getString(accessPath + "targetRevokedPassengerAccess", errorMessage);
        ALREADY_HAS_ACCESS = mm.deserialize(PREFIX
                + " " + config.getString(accessPath + "alreadyHasAccess", errorMessage));
        ALREADY_HAS_NO_ACCESS = mm.deserialize(PREFIX
                + " " + config.getString(accessPath + "alreadyHasNoAccess", errorMessage));
        DOES_NOT_SUPPORT_PASSENGERS = mm.deserialize(PREFIX
                + " " + config.getString(accessPath + "entityDoesNotSupportPassengers", errorMessage));
        ACCESS_NO_PLAYER = PREFIX
                + " " + config.getString(accessPath + "unableToFindPlayer", errorMessage);

        // --GPS
        String gpsPath = "messages.gps.";
        GPS_NO_MATCH = PREFIX
                + " " + config.getString(gpsPath + "noMatch", errorMessage);
        GPS_NO_PLAYERMETA = mm.deserialize(PREFIX
                + " " + config.getString(gpsPath + "noPlayerData", errorMessage));

        // --LIST
        String listPath = "messages.list.";
        LIST_DOES_NOT_EXIST_OR_OWN = mm.deserialize(PREFIX
                + " " + config.getString(listPath + "doesntExistOrOwn", errorMessage));
        LIST_NO_MATCH = mm.deserialize(PREFIX
                + " " + config.getString(listPath + "noneMatched", errorMessage));

        // --PERMISSION
        String permissionPath = "messages.permission.";
        PERMISSION_COMMAND = mm.deserialize(PREFIX
                + " " + config.getString(permissionPath + "command", errorMessage));
        PERMISSION_INTERACT = mm.deserialize(PREFIX
                + " " + config.getString(permissionPath + "interact", errorMessage));
        PERMISSION_MOUNT = mm.deserialize(PREFIX
                + " " + config.getString(permissionPath + "mount", errorMessage));

        // --SEAT SWAPPING
        String seatSwapPath = "messages.seatSwapping.";
        SEAT_SWAP_SUCCESS = PREFIX
                + " " + config.getString(seatSwapPath + "success", errorMessage);
        SEAT_SWAP_FAILURE = mm.deserialize(PREFIX
                + " " + config.getString(seatSwapPath + "failure", errorMessage));

        // --TAMING
        String tamingPath = "messages.taming.";
        TAMING_TO_THEMSELVES = mm.deserialize(PREFIX
                + " " + config.getString(tamingPath + "tamingToThemselves", errorMessage));
        TAMING_TO_OTHERS = PREFIX
                + " " + config.getString(tamingPath + "tamingToOthers", errorMessage);
        UNTAME = mm.deserialize(PREFIX
                + " " + config.getString(tamingPath + "untame", errorMessage));
        NOT_TAMED = mm.deserialize(PREFIX
                + " " + config.getString(tamingPath + "notTamed", errorMessage));
        TAMED_NOT_YOURS = mm.deserialize(PREFIX
                + " " + config.getString(tamingPath + "notYours", errorMessage));

        // --TELEPORT
        String teleportPath = "messages.teleport.";
        TELEPORT_TO = PREFIX
                + " " + config.getString(teleportPath + "teleportTo", errorMessage);
        TELEPORT_HERE = PREFIX
                + " " + config.getString(teleportPath + "teleportHere", errorMessage);
        TELEPORT_NO_PLAYER = PREFIX
                + " " + config.getString(teleportPath + "unableToFindPlayer", errorMessage);
        TELEPORT_NO_PLAYERMETA = mm.deserialize(PREFIX
                + " " + config.getString(teleportPath + "noPlayerData", errorMessage));
        TELEPORT_NO_MATCH = PREFIX
                + " " + config.getString(teleportPath + "noMatch", errorMessage);

        // --CLICK ACTIONS
        String clickActionsPath = "messages.clickActions.";
        CLICK_GRANT_FULL_ACCESS = PREFIX
                + " " + config.getString(clickActionsPath + "toGrantFullAccess", errorMessage);
        CLICK_GRANT_PASSENGER_ACCESS = PREFIX
                + " " + config.getString(clickActionsPath + "toGrantPassengerAccess", errorMessage);
        CLICK_REVOKE_FULL_ACCESS = PREFIX
                + " " + config.getString(clickActionsPath + "toRevokeFullAccess", errorMessage);
        CLICK_REVOKE_PASSENGER_ACCESS = PREFIX
                + " " + config.getString(clickActionsPath + "toRevokePassengerAccess", errorMessage);
        CLICK_TAME = PREFIX
                + " " + config.getString(clickActionsPath + "toTameToOthers", errorMessage);
        CLICK_UNTAME = PREFIX
                + " " + config.getString(clickActionsPath + "toUntame", errorMessage);
        CLICK_TIMEOUT = mm.deserialize(PREFIX
                + " " + config.getString(clickActionsPath + "timeout", errorMessage));
    }
}
