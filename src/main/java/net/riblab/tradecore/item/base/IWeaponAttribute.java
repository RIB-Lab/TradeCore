package net.riblab.tradecore.item.base;

import org.bukkit.entity.Player;

/**
 * 武器の特徴や攻撃方法を記述したクラス
 */
public interface IWeaponAttribute {

    /**
     * 攻撃速度
     */
    double getAttackSpeed();

    /**
     * 攻撃の威力
     */
    double getAttackDamage();

    boolean attack(Player player);
}
