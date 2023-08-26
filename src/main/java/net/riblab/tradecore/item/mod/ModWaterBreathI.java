package net.riblab.tradecore.item.mod;

import net.riblab.tradecore.modifier.IWaterBreatheLevelModifier;

public class ModWaterBreathI extends ItemMod implements IWaterBreatheLevelModifier {

    public ModWaterBreathI(int level) {
        super(level);
    }

    @Override
    public String getLore(){
        return "水中呼吸 +" + getLevel();
    }

    @Override
    public Integer apply(Integer originalValue, Integer modifiedValue) {
        return modifiedValue + 1;
    }
}
