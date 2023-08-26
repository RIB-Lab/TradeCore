package net.riblab.tradecore.item.mod;

import net.riblab.tradecore.modifier.IWalkSpeedModifier;

public class ModWalkSpeedI extends ItemMod implements IWalkSpeedModifier {

    public ModWalkSpeedI(int level) {
        super(level);
    }

    @Override
    public String getLore(){
        if(getLevel() > 0){
            return "歩行速度 +" + getLevel() + "%";
        }
        else{
            return "歩行速度 " + getLevel() + "%";
        }
    }

    @Override
    public Float apply(Float originalValue, Float modifiedValue) {
        return modifiedValue + originalValue * .01f * getLevel();
    }
}
