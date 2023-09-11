/*
 * Copyright (c) 2023. RIBLaB
 */
package net.riblab.tradecore.general.task;

import net.riblab.tradecore.general.Utils;
import net.riblab.tradecore.item.ItemUtils;
import net.riblab.tradecore.modifier.IEveryMinuteDurabilityModifier;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * 毎分発動するイベントを発行する
 */
class EveryMinutesEventRunnable extends BukkitRunnable {

    @Override
    public void run() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            int repairAmount = Utils.apply(player, 0, IEveryMinuteDurabilityModifier.class);
            ItemStack itemStack = player.getInventory().getItemInMainHand();
            player.getInventory().setItemInMainHand(ItemUtils.reduceDurabilityIfPossible(itemStack, -repairAmount));
        }
    }
}
