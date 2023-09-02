package net.riblab.tradecore.item.mod;

import net.riblab.tradecore.modifier.ISellPriceModifier;

/**
 * 売却可能なアイテムにつけるmod
 */
public class ModSellPriceI extends ItemMod<Double> implements ISellPriceModifier {

    public ModSellPriceI(Double level) {
        super(level);
    }

    @Override
    public String getLore() {
        return "売価：" + (Math.floor(this.getParam() * 100)) / 100 ;
    }

    @Override
    public Double apply(Double originalValue, Double modifiedValue) {
        return modifiedValue + this.getParam();
    }
}
