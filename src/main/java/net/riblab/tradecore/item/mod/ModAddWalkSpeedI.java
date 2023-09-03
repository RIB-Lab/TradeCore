package net.riblab.tradecore.item.mod;

import net.riblab.tradecore.modifier.IWalkSpeedModifier;

public class ModAddWalkSpeedI extends ItemMod<Integer> implements IWalkSpeedModifier {

    /**
     * @param level 何パーセント歩行速度を上昇/下降させるか
     */
    public ModAddWalkSpeedI(Integer level) {
        super(level);
    }

    @Override
    public String getLore() {
        if (this.getParam() > 0) {
            return "歩行速度 +" + this.getParam() + "%";
        } else {
            return "歩行速度 " + this.getParam() + "%";
        }
    }

    @Override
    public Float apply(Float originalValue, Float modifiedValue) {
        return modifiedValue + originalValue * .01f * (float) this.getParam();
    }
}