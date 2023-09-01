package net.riblab.tradecore.item.mod;

import net.riblab.tradecore.modifier.IEveryMinuteDurabilityModifier;
import net.riblab.tradecore.modifier.IMiningSpeedModifier;

public class ModMiningSpeedI extends ItemMod implements IMiningSpeedModifier {

    /**
     * 採掘スピードが1.2だとしたら100倍して120を代入して(int型の限界)
     * @param level　レベル
     */
    public ModMiningSpeedI(int level) {
        super(level);
    }

    @Override
    public String getLore() {
        return "採掘速度:" + (double) getLevel() / 100;
    }

    @Override
    public Double apply(Double originalValue, Double modifiedValue) {
        return modifiedValue + (double) getLevel() / 100;
    }
}
