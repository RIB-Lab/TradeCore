package net.riblab.tradecore.general;

import net.riblab.tradecore.item.base.IWeaponAttribute;

public interface IWeaponAttributeMelee extends IWeaponAttribute {
    
    double vanillaAttackSpeed = 4d;

    @Override
    default double getAttackSpeedForDisplay() {
        return vanillaAttackSpeed + getAttackSpeed();
    }
}
