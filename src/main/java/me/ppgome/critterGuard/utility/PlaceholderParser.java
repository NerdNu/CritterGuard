package me.ppgome.critterGuard.utility;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.KeybindComponent;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;

import java.util.ArrayList;
import java.util.List;

public class PlaceholderParser {

    private final String message;
    private final List<TagResolver.Single> placeholders = new ArrayList<>();
    private final KeybindComponent click = Component.keybind().keybind("key.use").build();
    private final MiniMessage mm = MiniMessage.miniMessage();

    private PlaceholderParser(String message) { this.message = message; }

    public static PlaceholderParser of(String message) { return new PlaceholderParser(message); }

    public PlaceholderParser player(String name) {
        placeholders.add(Placeholder.component("player", Component.text(name)));
        return this;
    }

    public PlaceholderParser identifier(String identifier) {
        placeholders.add(Placeholder.component("identifier", Component.text(identifier)));
        return this;
    }

    public PlaceholderParser mount(String mount) {
        placeholders.add(Placeholder.component("mount", Component.text(mount)));
        return this;
    }

    public PlaceholderParser click() {
        placeholders.add(Placeholder.component("button", click));
        return this;
    }

    public Component parse() {
        return mm.deserialize(message, placeholders.toArray(new TagResolver.Single[0]));
    }

}
