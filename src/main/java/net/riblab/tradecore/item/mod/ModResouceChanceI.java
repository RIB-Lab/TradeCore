package net.riblab.tradecore.item.mod;

import net.riblab.tradecore.modifier.IResourceChanceModifier;

/**
 * アイテムのドロップ率を上げるmod
 */
public class ModResouceChanceI extends ItemMod<Integer> implements IResourceChanceModifier {

    public ModResouceChanceI(Integer level) {
        super(level);
    }

    @Override
    public String getLore() {
        return "資源採取確率 + " + this.getParam() + "%";
    }

    @Override
    public Float apply(Float originalChance, Float modifiedChance) {
        return modifiedChance + originalChance * (float) this.getParam() * 0.01f;
    }
}
