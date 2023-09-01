package net.riblab.tradecore.item.base;

import org.bukkit.inventory.ItemStack;

/**
 * 耐久値を持つアイテム
 */
public interface IHasDurability {

    /**
     * ツールの基礎耐久値。-1で無限
     */
    int getBaseDurability();

    /**
     * ツールのインスタンスの耐久値を減らす / 回復させる
     */
    ItemStack reduceDurability(ItemStack instance, int amount);
}
