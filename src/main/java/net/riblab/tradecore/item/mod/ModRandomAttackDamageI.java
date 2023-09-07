/*
 * Copyright (c) 2023. RIBLaB
 */
package net.riblab.tradecore.item.mod;

import net.riblab.tradecore.modifier.IAttackDamageModifier;

import java.util.Optional;

public class ModRandomAttackDamageI extends ItemMod<Integer> implements IAttackDamageModifier {

    /**
     * @param level 　追加したいダメージの100倍
     */
    public ModRandomAttackDamageI(Integer level) {
        super(level);
    }

    @Override
    public Optional<String> getLore() {
        return Optional.of("攻撃力: " + ((double) this.getParam()) / 100);
    }

    @Override
    public Double apply(Double originalValue, Double modifiedValue) {
        return modifiedValue + ((double) this.getParam()) / 100;
    }
}
