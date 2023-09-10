/*
 * Copyright (c) 2023. RIBLaB
 */
package net.riblab.tradecore.item;

import net.riblab.tradecore.modifier.IToolStatsModifier;

import java.util.Map;

/**
 * バニラのルートテーブルとは別の概念
 *
 * @param materialSetKey      ドロップテーブルの対象となるバニラのブロックの種類(マテリアルセット)
 * @param toolType      ドロップテーブルを発動させるのに必要なツールの種類
 * @param harvestLevel  ドロップテーブルを発動させるために必要な最小のツールレベル
 * @param dropChanceMap ドロップ率(0~1)とその確率でドロップするアイテムのマップ
 */
public record LootTable(String materialSetKey,
                        IToolStatsModifier.ToolType toolType,
                        int harvestLevel,
                        Map<Float, String> dropChanceMap) {
}
