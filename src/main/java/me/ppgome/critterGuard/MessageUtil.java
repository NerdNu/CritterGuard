package me.ppgome.critterGuard;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Location;

public class MessageUtil {

    private static final MiniMessage miniMessage = MiniMessage.miniMessage();

    public static Component normalMessage(String prefix, String message) {
        return miniMessage.deserialize(prefix).append(Component.text(" " + message, NamedTextColor.GREEN));
    }

    public static Component warningMessage(String prefix, String message) {
        return miniMessage.deserialize(prefix).append(Component.text(" " + message, NamedTextColor.GOLD));
    }

    public static Component failedMessage(String prefix, String message) {
        return miniMessage.deserialize(prefix).append(Component.text(" " + message, NamedTextColor.RED));
    }

    public static String capitalizeFirstLetter(String str) {
        if (str == null || str.isEmpty()) return str;
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    public static Component locationBuilder(Location location, NamedTextColor color) {
        return Component.text("Location: " + location.getBlockX()
                + ", " + location.getBlockY()
                + ", " + location.getBlockZ()
                + " in world: " + location.getWorld().getName(), color);
    }

}
