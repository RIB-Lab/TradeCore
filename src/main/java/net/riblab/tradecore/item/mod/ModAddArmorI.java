/*
 * Copyright (c) 2023. RIBLaB 
 */
package net.riblab.tradecore.item.mod;

import net.riblab.tradecore.modifier.IArmorModifier;

public class ModAddArmorI extends ItemMod<Integer> implements IArmorModifier {

    public ModAddArmorI(Integer level) {
        super(level);
    }

    @Override
    public String getLore() {
        return "アーマー: " + this.getParam();
    }

    @Override
    public Double apply(Double originalValue, Double modifiedValue) {
        return modifiedValue + this.getParam();
    }
}
