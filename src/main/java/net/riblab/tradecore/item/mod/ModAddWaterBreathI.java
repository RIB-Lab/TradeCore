/*
 * Copyright (c) 2023. RIBLaB
 */
package net.riblab.tradecore.item.mod;

import net.riblab.tradecore.modifier.IWaterBreatheLevelModifier;

import java.util.Optional;

public class ModAddWaterBreathI extends ItemMod<Integer> implements IWaterBreatheLevelModifier {

    public ModAddWaterBreathI(Integer level) {
        super(level);
    }

    @Override
    public Optional<String> getLore() {
        return Optional.of("水中呼吸 +" + this.getParam());
    }

    @Override
    public Integer apply(Integer originalValue, Integer modifiedValue) {
        return modifiedValue + this.getParam();
    }
}
