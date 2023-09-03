package net.riblab.tradecore.item.mod;

import lombok.Getter;
import net.riblab.tradecore.item.impl.*;
import net.riblab.tradecore.modifier.IWeaponAttackModifier;
import org.bukkit.entity.Player;

import java.util.function.BiFunction;
import java.util.function.Function;

public class ModWeaponAttribute extends ItemMod<ModWeaponAttribute.WeaponType> implements IWeaponAttackModifier {

    public ModWeaponAttribute(WeaponType param) {
        super(param);
    }

    @Override
    public String getLore() {
        double rawAttackSpeedForDisplay = getParam().convertAttackSpeedToDisplayable == null ? getParam().attackSpeed : getParam().convertAttackSpeedToDisplayable.apply(getParam().attackSpeed);
        double formattedAttackSpeedForDisplay = Math.floor(rawAttackSpeedForDisplay * 100) / 100;
        return "攻撃速度: " + Math.floor(formattedAttackSpeedForDisplay * 100) / 100;
    }
    
    @Override
    public PackedAttackData apply(PackedAttackData originalValue, PackedAttackData modifiedValue) {
        modifiedValue.setResult(getParam().attackFunction.apply(modifiedValue.getPlayer(), modifiedValue.getDamage()));
        return modifiedValue;
    }

    public enum WeaponType{
        SWORD(WeaponAttributeSword::attack, -2.7d, WeaponType::getMeleeAttackSpeedForDisplay),
        SPEAR(WeaponAttributeSpear::attack, -3.2d, WeaponType::getMeleeAttackSpeedForDisplay),
        DAGGER(WeaponAttributeDagger::attack, -1.5d, WeaponType::getMeleeAttackSpeedForDisplay),
        BATTLEAXE(WeaponAttributeBattleAxe::attack, -3.6d, WeaponType::getMeleeAttackSpeedForDisplay),
        BOW(WeaponAttributeBow::attack, 1, null);

        /**
         * 敵を攻撃するfunction<br>
         * Player:プレイヤー、　Double:ダメージ, Boolean: 攻撃に成功したかどうか
         */
        private final BiFunction<Player, Double, Boolean> attackFunction;
        
        @Getter
        private final double attackSpeed;

        /**
         * 武器の攻撃速度(Double型)を表示用に変換するFunction
         * 
         */
        private final Function<Double, Double> convertAttackSpeedToDisplayable;
        private static final double vanillaAttackSpeed = 4.0d;

        WeaponType(BiFunction<Player, Double, Boolean> attackFunction, double attackSpeed, Function<Double, Double> convertAttackSpeedToDisplayable) {
            this.attackFunction = attackFunction;
            this.attackSpeed = attackSpeed;
            this.convertAttackSpeedToDisplayable = convertAttackSpeedToDisplayable;
        }
        
        public BiFunction<Player, Double, Boolean> get(){
            return attackFunction;
        }

        /**
         * 近接武器の攻撃速度はマイナスでないといけないため、表示する際バニラの攻撃速度を足す必要がある
         */
        public static double getMeleeAttackSpeedForDisplay(Double actualAttackSpeed){
            return actualAttackSpeed + vanillaAttackSpeed;
        }
    }
}
