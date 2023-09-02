package net.riblab.tradecore.item.mod;

import net.riblab.tradecore.modifier.IEveryMinuteDurabilityModifier;

public class ModEcologyI extends ItemMod<Integer> implements IEveryMinuteDurabilityModifier {

    public ModEcologyI(Integer level) {
        super(level);
    }

    @Override
    public String getLore() {
        return "エコロジー:" + this.getParam();
    }

    @Override
    public Integer apply(Integer originalValue, Integer modifiedValue) {
        return modifiedValue + (int) this.getParam();
    }
}
