package net.riblab.tradecore.item.mod;

import net.riblab.tradecore.modifier.IMiningSpeedModifier;

public class ModMiningSpeedI extends ItemMod<Double> implements IMiningSpeedModifier {

    /**
     * 採掘スピード1.2などを代入
     * @param level　レベル
     */
    public ModMiningSpeedI(Double level) {
        super(level);
    }

    @Override
    public String getLore() {
        return "採掘速度:" + Math.floor(this.getParam() * 100)  / 100;
    }

    @Override
    public Double apply(Double originalValue, Double modifiedValue) {
        return modifiedValue + this.getParam();
    }
}
