package net.riblab.tradecore.item.base;

import net.kyori.adventure.text.Component;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public interface ITCEquipment {
    /**
     * 装備の説明を生成する
     *
     * @param durability インスタンスが持つ耐久値
     * @return 装備の説明
     */
    List<Component> getLore(int durability);

    /**
     * 装備のインスタンスの耐久値を1減らす
     */
    ItemStack reduceDurability(ItemStack instance);

    List<net.riblab.tradecore.item.mod.IItemMod> getDefaultMods();

    int getBaseDurability();
}
