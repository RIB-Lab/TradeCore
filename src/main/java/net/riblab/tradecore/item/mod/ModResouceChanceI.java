package net.riblab.tradecore.item.mod;

import net.riblab.tradecore.modifier.IResourceChanceModifier;

public class ModResouceChanceI extends ItemMod implements IResourceChanceModifier {

    public ModResouceChanceI(int level) {
        super(level);
    }

    @Override
    public String getLore() {
        return "資源採取確率 + " + getLevel() + "%";
    }

    @Override
    public Float apply(Float originalChance, Float modifiedChance) {
        return modifiedChance + originalChance * (float)getLevel() * 0.01f;
    }
}
