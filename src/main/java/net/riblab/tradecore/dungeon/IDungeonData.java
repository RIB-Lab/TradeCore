package net.riblab.tradecore.dungeon;

import net.riblab.tradecore.entity.mob.ITCMob;
import net.riblab.tradecore.item.base.ITCItem;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.List;
import java.util.Map;

public interface IDungeonData<T> {
    String getName();

    Vector getSpawnPoint();

    List<ITCMob> getSpawnTable();

    int getBasePackSize();

    Class<? extends DungeonProgressionTracker<T>> getProgressionTracker();

    T getProgressionVariable();

    Map<ITCItem, Float> getRewardPool();
}
