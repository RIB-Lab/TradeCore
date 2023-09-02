package net.riblab.tradecore.item.base;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.riblab.tradecore.item.mod.IItemMod;

import java.util.ArrayList;
import java.util.List;

public interface IHasItemMod {

    /**
     * アイテムが既定で持つmodのリスト
     */
    List<IItemMod> getDefaultMods();

    /**
     * ツールに元からあるmodの説明文を取得する
     */
    default List<TextComponent> getDefaultModsLore(){
        List<TextComponent> texts = new ArrayList<>();

        for (IItemMod defaultMod : getDefaultMods()) {
            texts.add(Component.text(defaultMod.getLore()).decoration(TextDecoration.ITALIC, false).color(NamedTextColor.WHITE));
        }

        return texts;
    }
}
