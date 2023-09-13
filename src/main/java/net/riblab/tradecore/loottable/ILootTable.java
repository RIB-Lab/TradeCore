/*
 * Copyright (c) 2023. RIBLaB
 */
package net.riblab.tradecore.loottable;

import net.riblab.tradecore.general.ChanceFloat;
import net.riblab.tradecore.modifier.IToolStatsModifier.ToolType;

import java.util.List;
import java.util.Map;

public interface ILootTable {
    String getInternalName();
    
    String getMaterialSetKey();

    ToolType getToolType();

    int getHarvestLevel();

    Map<String, ChanceFloat> getDropChanceMap();

    List<ILootTableMod<?>> getMods();
}
