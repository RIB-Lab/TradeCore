package net.riblab.tradecore.dungeon;

public interface IDungeonData {
    String getName();

    org.bukkit.util.Vector getSpawnPoint();

    java.util.List<net.riblab.tradecore.mob.TCMob> getSpawnTable();

    int getBasePackSize();
}
