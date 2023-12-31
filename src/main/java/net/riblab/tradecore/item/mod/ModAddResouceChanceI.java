/*
 * Copyright (c) 2023. RIBLaB
 */
package net.riblab.tradecore.item.mod;

import net.riblab.tradecore.modifier.IResourceChanceModifier;

import java.util.Optional;

/**
 * アイテムのドロップ率を上げるmod
 */
public class ModAddResouceChanceI extends ItemMod<Integer> implements IResourceChanceModifier {

    public ModAddResouceChanceI(Integer level) {
        super(level);
    }

    @Override
    public Optional<String> getLore() {
        return Optional.of("資源採取確率 + " + this.getParam() + "%");
    }

    @Override
    public Float apply(Float originalChance, Float modifiedChance) {
        return modifiedChance + originalChance * (float) this.getParam() * 0.01f;
    }
}
