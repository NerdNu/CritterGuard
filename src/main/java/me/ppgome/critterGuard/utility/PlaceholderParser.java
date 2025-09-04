package me.ppgome.critterGuard.utility;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.KeybindComponent;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;

import java.util.ArrayList;
import java.util.List;

/**
 * This is a builder class for replacing placeholders in the plugin's configurable messages.
 */
public class PlaceholderParser {
    /**
     * The message being parsed.
     */
    private final String message;
    /**
     * The list of placeholders being replaced.
     */
    private final List<TagResolver.Single> placeholders = new ArrayList<>();
    /**
     * A pre-made KeybindComponent representing the key a player needs to press to interact with an entity.
     */
    private final KeybindComponent click = Component.keybind().keybind("key.use").build();
    /**
     * The instance of the MiniMessage API.
     */
    private final MiniMessage mm = MiniMessage.miniMessage();

    /**
     * Initializes the builder.
     *
     * @param message The message that will be parsed
     */
    private PlaceholderParser(String message) { this.message = message; }

    /**
     * Loads the message that contains placeholders into the object.
     *
     * @param message The message that will be parsed
     * @return The parsed message
     */
    public static PlaceholderParser of(String message) { return new PlaceholderParser(message); }

    /**
     * Adds a check for the <player> tag in the message and replaces it.
     *
     * @param name The name of the player that will replace <player>
     * @return The parsed message
     */
    public PlaceholderParser player(String name) {
        placeholders.add(Placeholder.component("player", Component.text(name)));
        return this;
    }

    /**
     * Adds a check for the <identifier> tag in the message and replaces it.
     *
     * @param identifier The player-provided identifier that will replace <identifier>
     * @return The parsed message
     */
    public PlaceholderParser identifier(String identifier) {
        placeholders.add(Placeholder.component("identifier", Component.text(identifier)));
        return this;
    }

    /**
     * Adds a check for the <mount> tag in the message and replaces it.
     *
     * @param mount The name or UUID of the mount that will replace <mount>
     * @return The parsed message
     */
    public PlaceholderParser mount(String mount) {
        placeholders.add(Placeholder.component("mount", Component.text(mount)));
        return this;
    }

    /**
     * Adds a check for the <click> tag in the message and replaces it.
     *
     * @return The parsed message
     */
    public PlaceholderParser click() {
        placeholders.add(Placeholder.component("button", click));
        return this;
    }

    /**
     * Parses the placeholders provided in the previous methods.
     *
     * @return The parsed message.
     */
    public Component parse() {
        return mm.deserialize(message, placeholders.toArray(new TagResolver.Single[0]));
    }

}
