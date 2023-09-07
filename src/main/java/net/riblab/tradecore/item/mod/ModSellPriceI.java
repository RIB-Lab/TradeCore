/*
 * Copyright (c) 2023. RIBLaB
 */
package net.riblab.tradecore.item.mod;

import net.riblab.tradecore.modifier.ISellPriceModifier;

import java.util.Optional;

/**
 * 売却可能なアイテムにつけるmod
 */
public class ModSellPriceI extends ItemMod<Double> implements ISellPriceModifier {

    public ModSellPriceI(Double level) {
        super(level);
    }

    @Override
    public Optional<String> getLore() {
        return Optional.of("売価：" + (Math.floor(this.getParam() * 100)) / 100);
    }

    @Override
    public Double apply(Double originalValue, Double modifiedValue) {
        return modifiedValue + this.getParam();
    }
}
