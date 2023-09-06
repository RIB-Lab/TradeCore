/*
 * Copyright (c) 2023. RIBLaB
 */
package net.riblab.tradecore.general.task;

import net.riblab.tradecore.config.DataService;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * 定期的にコンフィグを保存するタスク
 */
class ConfigSaveRunnable extends BukkitRunnable {
    @Override
    public void run() {
        DataService.getImpl().saveAll();
    }
}
