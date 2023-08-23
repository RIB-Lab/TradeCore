package net.riblab.tradecore.item.mod;

import net.riblab.tradecore.modifier.IEveryMinuteDurabilityModifier;

public class ModEcology extends ItemMod implements IEveryMinuteDurabilityModifier {

    public ModEcology(int level) {
        super(level);
    }

    @Override
    public String getLore(){
        return "エコロジー:" + getLevel();
    }

    @Override
    public Integer apply(Integer originalValue, Integer modifiedValue) {
        return modifiedValue + getLevel();
    }
}
