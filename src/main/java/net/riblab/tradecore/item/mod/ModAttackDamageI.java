package net.riblab.tradecore.item.mod;

import net.riblab.tradecore.modifier.IAttackDamageModifier;

public class ModAttackDamageI extends ItemMod<Integer> implements IAttackDamageModifier {

    /**
     * @param level　追加したいダメージの100倍
     */
    public ModAttackDamageI(Integer level) {
        super(level);
    }

    @Override
    public String getLore() {
        return "攻撃力: " + ((double) this.getParam()) / 100;
    }

    @Override
    public Double apply(Double originalValue, Double modifiedValue) {
        return modifiedValue + ((double) this.getParam()) / 100;
    }
}
