/*
 * Copyright (c) 2023. RIBLaB 
 */
package net.riblab.tradecore.item.mod;

import net.riblab.tradecore.item.base.DurabilityTable;
import net.riblab.tradecore.modifier.IRandomItemModCreator;

import java.util.List;

/**
 * このmodがあるアイテムはランダム化された最大耐久値をランダムmodとして持つことができる
 */
public class ModDefaultMaxDurabilityI extends ItemMod<DurabilityTable> implements IRandomItemModCreator {
    public ModDefaultMaxDurabilityI(DurabilityTable table) {
        super(table);
    }

    @Override
    public String getLore() {
        return null; //隠しパラメータ
    }
    
    @Override
    public List<IItemMod<?>> apply(List<IItemMod<?>> originalValue, List<IItemMod<?>> modifiedValue) {
        int dur = getParam().getRandomMaxDurability();
        modifiedValue.add(new ModRandomDurabilityI(new ModRandomDurabilityI.PackedDurabilityData(dur, dur)));
        return modifiedValue;
    }
}
