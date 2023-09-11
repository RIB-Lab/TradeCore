/*
 * Copyright (c) 2023. RIBLaB
 */
package net.riblab.tradecore.modifier;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.bukkit.entity.Player;

/**
 * プレイヤーに武器で攻撃させるmod
 */
public interface IWeaponAttackModifier extends IModifier<IWeaponAttackModifier.PackedAttackData> {

    @Data
    @AllArgsConstructor
    class PackedAttackData {
        /**
         * プレイヤー
         */
        Player player;
        /**
         * ダメージ
         */
        double damage;
        /**
         * 攻撃に成功したかどうか
         */
        boolean result;
    }
}
