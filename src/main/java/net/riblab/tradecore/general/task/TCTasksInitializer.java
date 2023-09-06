/*
 * Copyright (c) 2023. RIBLaB 
 */
package net.riblab.tradecore.general.task;

import net.riblab.tradecore.TradeCore;
import net.riblab.tradecore.config.DataService;
import net.riblab.tradecore.dungeon.DungeonService;
import net.riblab.tradecore.general.EventReciever;
import net.riblab.tradecore.general.Utils;
import net.riblab.tradecore.integration.TCEconomy;
import net.riblab.tradecore.item.ItemUtils;
import net.riblab.tradecore.item.base.ITCItem;
import net.riblab.tradecore.item.base.TCItems;
import net.riblab.tradecore.modifier.IEveryMinuteDurabilityModifier;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * プラグイン起動時から走り続けるBukkitRunnableのタスクたち
 */
public enum TCTasksInitializer {
    INSTANCE;

    private boolean isInit;

    //TODO:Runnableではなくリアル時間の経過でタスクを実行する(DateServiceを作って、毎tickSystemTimeが保存してあるDateより大きくないか確認する)
    public void init() {
        if (isInit)
            throw new RuntimeException("このプラグインの常駐タスクが2回初期化されようとしました");
        
        new ConfigSaveRunnable().runTaskTimerAsynchronously(TradeCore.getInstance(), 0, 3600);
        
        new PlayTicketsRunnable().runTaskTimer(TradeCore.getInstance(), 0, 12000);
        
        new EveryMinutesEventRunnable().runTaskTimer(TradeCore.getInstance(), 0, 1200);
        
        new DungeonKillerRunnable().runTaskTimer(TradeCore.getInstance(), 0, 6000);
        
        new EverySecondEventRunnable().runTaskTimer(TradeCore.getInstance(), 0, 20);

        isInit = true;
    }
}
