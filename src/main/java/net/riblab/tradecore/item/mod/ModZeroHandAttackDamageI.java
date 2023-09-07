/*
 * Copyright (c) 2023. RIBLaB
 */
package net.riblab.tradecore.item.mod;

import net.riblab.tradecore.modifier.IHandAttackDamageModifier;

import java.util.Optional;

public class ModZeroHandAttackDamageI extends ItemMod<Integer> implements IHandAttackDamageModifier {

    public ModZeroHandAttackDamageI(Integer dummy) {
        super(-1);
    }

    @Override
    public Optional<String> getLore() {
        return Optional.of("素手の殴りダメージ0");
    }

    @Override
    public Double apply(Double originalValue, Double modifiedValue) {
        return 0d;
    }
}
