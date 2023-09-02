package net.riblab.tradecore.item.mod;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.riblab.tradecore.modifier.IArmorModifier;
import net.riblab.tradecore.modifier.ISellPriceModifier;

/**
 * 売却可能なアイテムにつけるmod
 */
public class ModSellPriceI extends ItemMod implements ISellPriceModifier {

    public ModSellPriceI(double level) {
        super(level);
    }

    @Override
    public String getLore() {
        return "売価：" + (Math.floor(getLevel() * 100)) / 100 ;
    }

    @Override
    public Double apply(Double originalValue, Double modifiedValue) {
        return modifiedValue + getLevel();
    }
}
