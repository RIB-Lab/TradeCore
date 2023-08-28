package net.riblab.tradecore.item.mod;

import net.riblab.tradecore.modifier.IArmorModifier;

public class ModAddArmorI extends ItemMod implements IArmorModifier {

    public ModAddArmorI(int level) {
        super(level);
    }

    @Override
    public String getLore() {
        return "アーマー: " + getLevel();
    }

    @Override
    public Double apply(Double originalValue, Double modifiedValue) {
        return modifiedValue + getLevel();
    }
}
