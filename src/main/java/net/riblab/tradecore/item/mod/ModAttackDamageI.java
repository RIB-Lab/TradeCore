package net.riblab.tradecore.item.mod;

import net.riblab.tradecore.modifier.IArmorModifier;
import net.riblab.tradecore.modifier.IAttackDamageModifier;

public class ModAttackDamageI extends ItemMod implements IAttackDamageModifier {

    /**
     * @param level　追加したいダメージの100倍
     */
    public ModAttackDamageI(int level) {
        super(level);
    }

    @Override
    public String getLore() {
        return "攻撃力: " + (double)getLevel() / 100;
    }

    @Override
    public Double apply(Double originalValue, Double modifiedValue) {
        return modifiedValue + (double)getLevel() / 100;
    }
}
