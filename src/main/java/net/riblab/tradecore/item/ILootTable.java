/*
 * Copyright (c) 2023. RIBLaB
 */
package net.riblab.tradecore.item;

import net.riblab.tradecore.modifier.IToolStatsModifier.ToolType;

import java.util.Map;

public interface ILootTable {
    String getInternalName();
    
    String getMaterialSetKey();

    ToolType getToolType();

    int getHarvestLevel();

    Map<Float, String> getDropChanceMap();
}
