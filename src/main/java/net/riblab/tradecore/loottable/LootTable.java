/*
 * Copyright (c) 2023. RIBLaB
 */
package net.riblab.tradecore.loottable;

import lombok.Getter;
import lombok.Setter;
import net.riblab.tradecore.general.ChanceFloat;
import net.riblab.tradecore.modifier.IToolStatsModifier;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * バニラのルートテーブルとは別の概念
 */
public final class LootTable implements ILootTable {
    
    @Getter @Setter
    private String internalName;
    
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
    private Map<String, ChanceFloat> dropChanceMap;

    @Getter @Setter
    private List<ILootTableMod<?>> mods = new ArrayList<>();
    
    public LootTable() {
    }
}
