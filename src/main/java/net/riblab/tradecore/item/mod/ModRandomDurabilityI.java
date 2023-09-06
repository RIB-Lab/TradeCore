/*
 * Copyright (c) 2023. RIBLaB 
 */
package net.riblab.tradecore.item.mod;

import lombok.AllArgsConstructor;
import lombok.Data;
import net.riblab.tradecore.modifier.IDurabilityModifier;

public class ModRandomDurabilityI extends ItemMod<ModRandomDurabilityI.PackedDurabilityData> implements IDurabilityModifier {

    /**
     * アイテムのランダムな最大耐久値と現在の耐久値を保存するmod。アイテム内に焼きこまれる
     * @param data　耐久値
     */
    public ModRandomDurabilityI(PackedDurabilityData data) {
        super(data);
    }

    @Override
    public String getLore() {
        return "耐久値: " + getParam().getCurrentDur() + "/" + getParam().getMaxDur();
    }

    @Override
    public Integer apply(Integer originalValue, Integer modifiedValue) {
        return -1; //保存専用modです
    }

    @Data
    @AllArgsConstructor
    public static class PackedDurabilityData{
        int currentDur;
        int maxDur;
    }
}
