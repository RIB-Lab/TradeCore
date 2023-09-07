/*
 * Copyright (c) 2023. RIBLaB
 */
package net.riblab.tradecore.item.mod;

import net.riblab.tradecore.modifier.IMiningSpeedModifier;

import java.util.Optional;

public class ModRandomMiningSpeedI extends ItemMod<Double> implements IMiningSpeedModifier {

    /**
     * 採掘スピード1.2などを代入
     *
     * @param level 　レベル
     */
    public ModRandomMiningSpeedI(Double level) {
        super(level);
    }

    @Override
    public Optional<String> getLore() {
        return Optional.of("採掘速度:" + Math.floor(this.getParam() * 100) / 100);
    }

    @Override
    public Double apply(Double originalValue, Double modifiedValue) {
        return modifiedValue + Math.log10(this.getParam());
    }
}
