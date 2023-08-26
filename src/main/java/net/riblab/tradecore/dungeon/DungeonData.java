package net.riblab.tradecore.dungeon;

import lombok.Data;
import net.riblab.tradecore.mob.ITCMob;
import org.bukkit.util.Vector;

import java.util.List;

@Data
public class DungeonData implements IDungeonData {
    private final String name;
    private final Vector spawnPoint;
    private final List<ITCMob> spawnTable;
    private final int basePackSize;
}
