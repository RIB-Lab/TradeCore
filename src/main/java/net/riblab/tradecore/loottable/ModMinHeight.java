/*
 * Copyright (c) 2023. RIBLaB
 */
package net.riblab.tradecore.loottable;

import net.riblab.tradecore.modifier.IHeightModifier;

/**
 * LootTableが適用される最小高度を決めるmod
 */
public class ModMinHeight extends LootTableMod<Integer> implements IHeightModifier {

    public ModMinHeight(Integer level) {
        super(level);
    }

    @Override
    public Integer apply(Integer originalValue, Integer modifiedValue) {
        return getParam();
    }

    @Override
    public String toString(){
        return "採取できる最小高度: " + getParam();
    }
}
