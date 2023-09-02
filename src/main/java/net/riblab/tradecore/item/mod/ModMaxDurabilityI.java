package net.riblab.tradecore.item.mod;

import net.riblab.tradecore.modifier.IDurabilityModifier;

public class ModMaxDurabilityI extends ItemMod<Integer> implements IDurabilityModifier {

    /**
     * アイテムの最大耐久値を決めるmod
     * @param level　耐久値
     */
    public ModMaxDurabilityI(Integer level) {
        super(level);
    }

    @Override
    public String getLore() {
        return "採掘速度:" + this.getParam() / 100;
    }

    @Override
    public Integer apply(Integer originalValue, Integer modifiedValue) {
        return modifiedValue + (int) this.getParam();
    }
}
