package net.riblab.tradecore.item.mod;

import net.riblab.tradecore.modifier.IDurabilityModifier;

public class ModMaxDurabilityI extends ItemMod implements IDurabilityModifier {

    /**
     * アイテムの最大耐久値を決めるmod
     * @param level　耐久値
     */
    public ModMaxDurabilityI(int level) {
        super(level);
    }

    @Override
    public String getLore() {
        return "採掘速度:" + getLevel() / 100;
    }

    @Override
    public Integer apply(Integer originalValue, Integer modifiedValue) {
        return modifiedValue + (int) getLevel();
    }
}
