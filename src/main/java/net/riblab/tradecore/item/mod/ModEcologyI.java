/*
 * Copyright (c) 2023. RIBLaB
 */
package net.riblab.tradecore.item.mod;

import net.riblab.tradecore.modifier.IEveryMinuteDurabilityModifier;

import java.util.Optional;

public class ModEcologyI extends ItemMod<Integer> implements IEveryMinuteDurabilityModifier {

    public ModEcologyI(Integer level) {
        super(level);
    }

    @Override
    public Optional<String> getLore() {
        return Optional.of("エコロジー:" + this.getParam());
    }

    @Override
    public Integer apply(Integer originalValue, Integer modifiedValue) {
        return modifiedValue + (int) this.getParam();
    }
}
