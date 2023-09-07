/*
 * Copyright (c) 2023. RIBLaB
 */
package net.riblab.tradecore.item.mod;

import lombok.Getter;
import net.riblab.tradecore.general.AttackCooldownService;
import net.riblab.tradecore.item.impl.*;
import net.riblab.tradecore.modifier.IWeaponAttackModifier;
import org.bukkit.entity.Player;

import java.util.Optional;
import java.util.function.BiFunction;

public class ModWeaponAttribute extends ItemMod<ModWeaponAttribute.WeaponType> implements IWeaponAttackModifier {

    public ModWeaponAttribute(WeaponType param) {
        super(param);
    }

    @Override
    public Optional<String> getLore() {
        double rawAttackSpeedForDisplay = getParam().attackSpeed > 0 ? getParam().attackSpeed : WeaponType.getMeleeAttackSpeedForDisplay(getParam().attackSpeed);
        double formattedAttackSpeedForDisplay = Math.floor(rawAttackSpeedForDisplay * 100) / 100;
        return Optional.of("攻撃速度:" + Math.floor(formattedAttackSpeedForDisplay * 100) / 100);
    }

    @Override
    public PackedAttackData apply(PackedAttackData originalValue, PackedAttackData modifiedValue) {
        modifiedValue.setResult(getParam().attackFunction.apply(modifiedValue.getPlayer(), modifiedValue.getDamage()));
        if (getParam().attackSpeed > 0)
            AttackCooldownService.getImpl().add(modifiedValue.getPlayer(), getParam().attackSpeed);

        return modifiedValue;
    }

    public enum WeaponType {
        SWORD(WeaponAttributeSword::attack, -2.7d),
        SPEAR(WeaponAttributeSpear::attack, -3.2d),
        DAGGER(WeaponAttributeDagger::attack, -1.5d),
        BATTLEAXE(WeaponAttributeBattleAxe::attack, -3.6d),
        BOW(WeaponAttributeBow::attack, 1);

        /**
         * 敵を攻撃するfunction<br>
         * Player:プレイヤー、　Double:ダメージ, Boolean: 攻撃に成功したかどうか
         */
        private final BiFunction<Player, Double, Boolean> attackFunction;

        /**
         * 攻撃速度。近接武器なら-4~0にする、遠隔武器なら0より大きくする（攻撃速度で近接か遠隔かの処理が分かれる）
         */
        @Getter
        private final double attackSpeed;
        private static final double vanillaAttackSpeed = 4.0d;

        WeaponType(BiFunction<Player, Double, Boolean> attackFunction, double attackSpeed) {
            this.attackFunction = attackFunction;
            this.attackSpeed = attackSpeed;
        }

        public BiFunction<Player, Double, Boolean> get() {
            return attackFunction;
        }

        /**
         * 近接武器の攻撃速度はマイナスでないといけないため、表示する際バニラの攻撃速度を足す必要がある
         */
        public static double getMeleeAttackSpeedForDisplay(Double actualAttackSpeed) {
            return actualAttackSpeed + vanillaAttackSpeed;
        }
    }
}
