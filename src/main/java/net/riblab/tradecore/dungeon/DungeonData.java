package net.riblab.tradecore.dungeon;

import lombok.Data;
import net.riblab.tradecore.mob.TCMob;
import org.bukkit.util.Vector;

import java.util.List;

@Data
public class DungeonData{
    private final String name;
    private final Vector spawnPoint;
    private final List<TCMob> spawnTable;
    private final int basePackSize;
}
