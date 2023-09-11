/*
 * Copyright (c) 2023. RIBLaB
 */
package net.riblab.tradecore.general.task;

import net.riblab.tradecore.TradeCore;
import net.riblab.tradecore.general.ErrorMessages;

/**
 * プラグイン起動時から走り続けるBukkitRunnableのタスクたち
 */
public enum TCTasksInitializer {
    INSTANCE;

    private boolean isInit;

    //TODO:Runnableではなくリアル時間の経過でタスクを実行する(DateServiceを作って、毎tickSystemTimeが保存してあるDateより大きくないか確認する)
    public void init() {
        if (isInit)
            throw new RuntimeException(ErrorMessages.TASK_INIT_TWO_TIMES.get());

        new ConfigSaveRunnable().runTaskTimerAsynchronously(TradeCore.getInstance(), 0, 3600);

        new PlayTicketsRunnable().runTaskTimer(TradeCore.getInstance(), 0, 12000);

        new EveryMinutesEventRunnable().runTaskTimer(TradeCore.getInstance(), 0, 1200);

        new DungeonKillerRunnable().runTaskTimer(TradeCore.getInstance(), 0, 6000);

        new EverySecondEventRunnable().runTaskTimer(TradeCore.getInstance(), 0, 20);

        isInit = true;
    }
}
