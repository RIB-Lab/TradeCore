package net.riblab.tradecore.item.mod;

import lombok.AllArgsConstructor;
import lombok.Data;
import net.riblab.tradecore.item.base.AttackDamageSpread;
import net.riblab.tradecore.modifier.IRandomItemModCreator;

import java.util.List;

/**
 * アイテムが生成されたときのランダム攻撃ダメージを決めるmod
 */
public class ModDefaultAttackDamageI extends ItemMod<ModDefaultAttackDamageI.DamageData> implements IRandomItemModCreator {

    public ModDefaultAttackDamageI(DamageData param) {
        super(param);
    }

    @Override
    public String getLore() {
        return null; //隠しパラメータ
    }

    @Override
    public List<IItemMod<?>> apply(List<IItemMod<?>> originalValue, List<IItemMod<?>> modifiedValue) {
        modifiedValue.add(new ModRandomAttackDamageI((int)(getParam().spread.getRandomDamage(getParam().baseDamage) * 100)));
        return modifiedValue;
    }
    
    @Data
    @AllArgsConstructor
    public static class DamageData{
        double baseDamage;
        AttackDamageSpread spread;
    }
}
