package net.riblab.tradecore.item.mod;

import net.riblab.tradecore.item.base.MiningSpeedTable;
import net.riblab.tradecore.modifier.IRandomItemModCreator;

import java.util.List;

public class ModDefaultMiningSpeedI extends ItemMod<MiningSpeedTable> implements IRandomItemModCreator {
    public ModDefaultMiningSpeedI(MiningSpeedTable param) {
        super(param);
    }

    @Override
    public String getLore() {
        return null; //隠しパラメータ
    }

    @Override
    public List<IItemMod<?>> apply(List<IItemMod<?>> originalValue, List<IItemMod<?>> modifiedValue) {
        double speed = getParam().getRandomMiningSpeed();
        modifiedValue.add(new ModRandomMiningSpeedI(speed));
        return modifiedValue;
    }
}
