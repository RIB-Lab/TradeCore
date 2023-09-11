/*
 * Copyright (c) 2023. RIBLaB
 */
package net.riblab.tradecore.general.task;

import net.riblab.tradecore.integration.TCEconomy;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * 10分に1回全てのプレイヤーにプレイチケットを配布する
 */
class PlayTicketsRunnable extends BukkitRunnable {

    private TCEconomy getEconomy() {
        return TCEconomy.getImpl();
    }

    @Override
    public void run() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            getEconomy().depositTickets(player, 1);
        }
    }
}
