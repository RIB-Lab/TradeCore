package net.riblab.tradecore.config;

import de.exlll.configlib.Serializer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.serializer.ComponentSerializer;

public class TextComponentSerializer implements Serializer<TextComponent, String> {
    
    @Override
    public String serialize(TextComponent element) {
        return element.content();
    }

    @Override
    public TextComponent deserialize(String element) {
        return Component.text(element);
    }
}
