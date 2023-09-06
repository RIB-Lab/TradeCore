/*
 * Copyright (c) 2023. RIBLaB
 */
package net.riblab.tradecore.general.task;

import net.riblab.tradecore.general.EventReciever;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * 毎秒イベントを発行する
 */
public class EverySecondEventRunnable extends BukkitRunnable {
    @Override
    public void run() {
        EventReciever.INSTANCE.onSecondPassed();
    }
}
