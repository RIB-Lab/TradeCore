/*
 * Copyright (c) 2023. RIBLaB
 */
package net.riblab.tradecore.dungeon;

import lombok.AllArgsConstructor;
import lombok.Data;
import net.riblab.tradecore.entity.mob.ITCMob;
import net.riblab.tradecore.item.base.ITCItem;
import org.bukkit.util.Vector;

import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
final class DungeonData<T> implements IDungeonData<T> {

    /**
     * ダンジョンの表示名
     */
    private final String name;

    /**
     * ダンジョンの内部名称
     */
    private final String internalName;

    /**
     * ダンジョンのスポーン地点
     */
    private final Vector spawnPoint;

    /**
     * ダンジョンで沸く敵のスポーンテーブル
     */
    private final List<ITCMob> spawnTable;

    /**
     * ダンジョンで敵が一度に沸くときの数量
     */
    private final int basePackSize;

    /**
     * ダンジョンの進捗トラッカーのクラス
     */
    private Class<? extends DungeonProgressionTracker<T>> progressionTracker;

    /**
     * 進捗トラッカーが参照する変数
     */
    private T progressionVariable;

    /**
     * 報酬プール
     */
    private final Map<String, Float> rewardPool;
}
