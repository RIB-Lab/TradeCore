package net.riblab.tradecore.item.attribute;

import net.riblab.tradecore.item.mod.ItemMod;

import java.util.List;

public interface IHasItemMod {

    /**
     * アイテムが既定で持つmodのリスト
     */
    List<ItemMod> getDefaultMods();
}
