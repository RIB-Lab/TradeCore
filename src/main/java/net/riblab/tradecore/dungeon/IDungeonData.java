package net.riblab.tradecore.dungeon;

import net.riblab.tradecore.mob.ITCMob;
import org.bukkit.util.Vector;

import java.util.List;

public interface IDungeonData {
    String getName();

    Vector getSpawnPoint();

    List<ITCMob> getSpawnTable();

    int getBasePackSize();
}
