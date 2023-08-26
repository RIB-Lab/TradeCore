package net.riblab.tradecore.dungeon;

import net.riblab.tradecore.mob.ITCMob;

public interface IDungeonData {
    String getName();

    org.bukkit.util.Vector getSpawnPoint();

    java.util.List<ITCMob> getSpawnTable();

    int getBasePackSize();
}
