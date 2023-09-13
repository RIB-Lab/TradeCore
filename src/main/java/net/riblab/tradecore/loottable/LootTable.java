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

    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append("ルートテーブル: ").append(internalName).append("\n")
                .append("対象のマテリアルセット: ").append(materialSetKey).append("\n")
                .append("適正ツール: ").append(toolType.toString()).append("\n")
                .append("採取レベル: ").append(harvestLevel).append("\n");
        if(!mods.isEmpty()){
            sb.append("mods:").append("\n");
            for (ILootTableMod<?> mod : mods) {
                sb.append("    ").append(mod.toString()).append("\n");
            }
        }
        sb.append("採れるものリスト: ").append("\n");
        dropChanceMap.forEach((s, aFloat) -> sb.append("    ").append(s).append(": ").append(aFloat.get()).append("%").append("\n"));
        
        return  sb.toString();
    }
}
