package net.riblab.tradecore.item.mod;

import net.riblab.tradecore.modifier.IHandAttackDamageModifier;

public class ModZeroHandAttackDamageI extends ItemMod<Integer> implements IHandAttackDamageModifier {

    public ModZeroHandAttackDamageI(Integer dummy) {
        super(-1);
    }

    @Override
    public String getLore() {
        return "素手の殴りダメージ0";
    }

    @Override
    public Double apply(Double originalValue, Double modifiedValue) {
        return 0d;
    }
}
