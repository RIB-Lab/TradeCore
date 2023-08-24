package net.riblab.tradecore.item.weapon;

import net.riblab.tradecore.item.attribute.IHasDurability;
import net.riblab.tradecore.item.attribute.IHasItemMod;

public interface ITCWeapon extends IHasDurability, IHasItemMod {

    /**
     * 武器の性質を取得する
     * @return
     */
    IWeaponAttribute getAttribute();
}