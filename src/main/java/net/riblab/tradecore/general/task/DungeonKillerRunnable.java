/*
 * Copyright (c) 2023. RIBLaB
 */
package net.riblab.tradecore.general.task;

import net.riblab.tradecore.dungeon.DungeonService;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * 定期的に誰もいないダンジョンを削除する
 */
public class DungeonKillerRunnable extends BukkitRunnable {

    @Override
    public void run() {
        DungeonService.getImpl().killEmptyDungeons();
    }
}
