/*
 * Copyright (c) 2023. RIBLaB
 */
package net.riblab.tradecore.item;

import net.riblab.tradecore.item.base.ITCItem;
import net.riblab.tradecore.modifier.IToolStatsModifier;
import org.bukkit.Material;

import java.util.Map;
import java.util.Set;

/**
 * バニラのルートテーブルとは別の概念
 *
 * @param material      ドロップテーブルの対象となるバニラのブロックの種類
 * @param toolType      ドロップテーブルを発動させるのに必要なツールの種類
 * @param harvestLevel  ドロップテーブルを発動させるために必要な最小のツールレベル
 * @param dropChanceMap ドロップ率(0~1)とその確率でドロップするアイテムのマップ
 */
public record LootTable(Set<Material> material,
                        IToolStatsModifier.ToolType toolType,
                        int harvestLevel,
                        Map<Float, String> dropChanceMap) {
}
