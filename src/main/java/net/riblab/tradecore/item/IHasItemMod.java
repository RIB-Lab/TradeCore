package net.riblab.tradecore.item;

import net.riblab.tradecore.item.mod.ItemMod;

import java.util.List;

public interface IHasItemMod {

    /**
     * アイテムが既定で持つmodのリスト
     */
    List<ItemMod> getDefaultMods();
}
