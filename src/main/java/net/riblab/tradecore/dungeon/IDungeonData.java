package net.riblab.tradecore.dungeon;

import net.riblab.tradecore.mob.ITCMob;
import org.bukkit.util.Vector;

import java.util.List;

public interface IDungeonData<T> {
    String getName();

    Vector getSpawnPoint();

    List<ITCMob> getSpawnTable();

    int getBasePackSize();

    Class<? extends DungeonProgressionTracker<T>> getProgressionTracker();
    
    T getProgressionVariable();
}
