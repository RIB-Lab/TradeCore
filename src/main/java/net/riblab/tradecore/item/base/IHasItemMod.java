package net.riblab.tradecore.item.base;

import net.riblab.tradecore.item.mod.IItemMod;

import java.util.List;

public interface IHasItemMod {

    /**
     * アイテムが既定で持つmodのリスト
     */
    List<IItemMod> getDefaultMods();
}
