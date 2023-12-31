/*
 * Copyright (c) 2023. RIBLaB
 */
package net.riblab.tradecore.dungeon;

import net.riblab.tradecore.entity.mob.ITCMob;
import net.riblab.tradecore.item.base.ITCItem;
import org.bukkit.util.Vector;

import java.util.List;
import java.util.Map;

public sealed interface IDungeonData<T> permits DungeonData {
    String getName();
    
    String getInternalName();

    Vector getSpawnPoint();

    List<ITCMob> getSpawnTable();

    int getBasePackSize();

    Class<? extends DungeonProgressionTracker<T>> getProgressionTracker();

    T getProgressionVariable();

    Map<String, Float> getRewardPool();
}
