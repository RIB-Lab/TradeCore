/*
 * Copyright (c) 2023. RIBLaB
 */
package net.riblab.tradecore.item.mod;

import net.riblab.tradecore.modifier.IArmorModifier;

import java.util.Optional;

public class ModAddArmorI extends ItemMod<Integer> implements IArmorModifier {

    public ModAddArmorI(Integer level) {
        super(level);
    }

    @Override
    public Optional<String> getLore() {
        return Optional.of("アーマー: " + this.getParam());
    }

    @Override
    public Double apply(Double originalValue, Double modifiedValue) {
        return modifiedValue + this.getParam();
    }
}
