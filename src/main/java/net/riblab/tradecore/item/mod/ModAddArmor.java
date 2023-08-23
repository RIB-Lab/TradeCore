package net.riblab.tradecore.item.mod;

import net.riblab.tradecore.modifier.IArmorModifier;

public class ModAddArmor extends ItemMod implements IArmorModifier {

    public ModAddArmor(int level) {
        super(level);
    }

    @Override
    public String getLore(){
        return "アーマー: " + getLevel();
    }

    @Override
    public Double apply(Double originalValue, Double modifiedValue) {
        return modifiedValue + getLevel();
    }
}
