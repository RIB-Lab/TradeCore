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

    /**
     * 攻撃する
     * @return 攻撃が有効かどうか(攻撃によって武器の耐久を減らす必要があるか)
     */
    boolean attack(Player player);

    /**
     * ツールチップに表示用の攻撃速度
     */
    default double getAttackSpeedForDisplay(){
        return getAttackSpeed();
    }
}
