package net.riblab.tradecore.item.base;

public interface ITCWeapon extends IHasDurability {

    /**
     * 武器の性質を取得する
     */
    IWeaponAttribute getAttribute();
    
    AttackDamageSpread getAttackDamageSpread();
}