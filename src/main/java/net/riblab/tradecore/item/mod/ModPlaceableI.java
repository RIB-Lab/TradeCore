/*
 * Copyright (c) 2023. RIBLaB 
 */
package net.riblab.tradecore.item.mod;

import net.riblab.tradecore.modifier.IPlaceableModifier;

/**
 * カスタムアイテムを設置可能にする
 */
public class ModPlaceableI extends ItemMod<Boolean> implements IPlaceableModifier {
    public ModPlaceableI(Boolean isPlaceable) {
        super(true);
    }

    @Override
    public String getLore() {
        return "設置可能";
    }
    
    @Override
    public Boolean apply(Boolean originalValue, Boolean modifiedValue) {
        return true;
    }
}
