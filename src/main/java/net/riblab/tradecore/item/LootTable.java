/*
 * Copyright (c) 2023. RIBLaB
 */
package net.riblab.tradecore.item;

import lombok.Getter;
import lombok.Setter;
import net.riblab.tradecore.modifier.IToolStatsModifier;

import java.util.Map;

/**
 * バニラのルートテーブルとは別の概念
 */
public final class LootTable implements ILootTable {
    /**
     * ドロップテーブルの対象となるバニラのブロックの種類(マテリアルセット)
     */
    @Getter @Setter
    private String materialSetKey;
    
    /**
     * ドロップテーブルを発動させるのに必要なツールの種類
     */
    @Getter @Setter
    private IToolStatsModifier.ToolType toolType;
    
    /**
     * ドロップテーブルを発動させるために必要な最小のツールレベル
     */
    @Getter @Setter
    private int harvestLevel;
    
    /**
     * ドロップ率(0~1)とその確率でドロップするアイテムのマップ
     */
    @Getter @Setter
    private Map<Float, String> dropChanceMap;
    
    public LootTable() {
    }

    public LootTable(String materialSetKey,
                     IToolStatsModifier.ToolType toolType,
                     int harvestLevel,
                     Map<Float, String> dropChanceMap) {
        this.materialSetKey = materialSetKey;
        this.toolType = toolType;
        this.harvestLevel = harvestLevel;
        this.dropChanceMap = dropChanceMap;
    }

}
