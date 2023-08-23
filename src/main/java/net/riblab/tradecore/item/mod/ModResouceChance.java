package net.riblab.tradecore.item.mod;

import net.riblab.tradecore.modifier.IResourceChanceModifier;

public class ModResouceChance extends ItemMod implements IResourceChanceModifier {

    public ModResouceChance(int level) {
        super(level);
    }

    @Override
    public String getLore(){
        return "資源採取確率 + " + getLevel() + "%";
    }

    @Override
    public Float apply(Float originalChance, Float modifiedChance) {
        return modifiedChance + originalChance * (getLevel() * 0.01f);
    }
}
