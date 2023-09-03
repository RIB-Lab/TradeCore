package net.riblab.tradecore.item.mod;

import lombok.Getter;
import net.riblab.tradecore.item.impl.*;
import net.riblab.tradecore.modifier.IWeaponAttackModifier;
import org.bukkit.entity.Player;

import java.util.function.BiFunction;

public class ModWeaponAttribute extends ItemMod<ModWeaponAttribute.WeaponType> implements IWeaponAttackModifier {

    public ModWeaponAttribute(WeaponType param) {
        super(param);
    }

    @Override
    public String getLore() {
        return "攻撃速度: " + Math.floor(getParam().attackSpeed * 100) / 100;
    }
    
    @Override
    public PackedAttackData apply(PackedAttackData originalValue, PackedAttackData modifiedValue) {
        modifiedValue.setResult(getParam().attackFunction.apply(modifiedValue.getPlayer(), modifiedValue.getDamage()));
        return modifiedValue;
    }

    public enum WeaponType{
        SWORD(WeaponAttributeSword::attack, -2.7d),
        SPEAR(WeaponAttributeSpear::attack, -3.2d),
        DAGGER(WeaponAttributeDagger::attack, -1.5d),
        BATTLEAXE(WeaponAttributeBattleAxe::attack, -3.6d),
        BOW(WeaponAttributeBow::attack, 1);

        /**
         * 敵を攻撃するfunction
         * Player:プレイヤー、　Double:ダメージ, Boolean: 攻撃に成功したかどうか
         */
        private final BiFunction<Player, Double, Boolean> attackFunction;
        
        @Getter
        private final double attackSpeed;

        WeaponType(BiFunction<Player, Double, Boolean> attackFunction, double attackSpeed) {
            this.attackFunction = attackFunction;
            this.attackSpeed = attackSpeed;
        }
        
        public BiFunction<Player, Double, Boolean> get(){
            return attackFunction;
        }
    }
}
