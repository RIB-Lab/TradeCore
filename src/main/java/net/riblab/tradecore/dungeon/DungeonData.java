package net.riblab.tradecore.dungeon;

import lombok.Data;
import net.riblab.tradecore.mob.ITCMob;
import org.bukkit.util.Vector;

import java.util.List;

@Data
public class DungeonData implements IDungeonData {

    /**
     * ダンジョン名
     */
    private final String name;

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
}
