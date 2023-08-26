package net.riblab.tradecore;

import net.milkbowl.vault.economy.Economy;
import net.riblab.tradecore.integration.EconomyImpl;
import net.riblab.tradecore.integration.TCEconomy;
import net.riblab.tradecore.item.base.IHasDurability;
import net.riblab.tradecore.item.base.ITCItem;
import net.riblab.tradecore.item.TCItems;
import net.riblab.tradecore.modifier.IEveryMinuteDurabilityModifier;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * プラグイン起動時から走り続けるBukkitRunnableのタスクたち
 */
public class TCTasks {
    
    private TCEconomy getEconomy(){
        return TradeCore.getInstance().getEconomy();
    }
    
    public TCTasks(){
        //定期的にコンフィグを保存
        new BukkitRunnable() {
            @Override
            public void run() {
                TradeCore.getInstance().getConfigManager().save();
            }
        }.runTaskTimer(TradeCore.getInstance(), 0, 3600);

        //10分に1回プレイチケット配布
        new BukkitRunnable() {
            @Override
            public void run() {
                Bukkit.getOnlinePlayers().forEach(player -> getEconomy().depositTickets(player, 1));
            }
        }.runTaskTimer(TradeCore.getInstance(), 0, 12000);

        //毎分発動するmodifier系イベント
        new BukkitRunnable() {
            @Override
            public void run() {
                Bukkit.getOnlinePlayers().forEach(player -> {
                    int repairAmount = Utils.apply(player, 0, IEveryMinuteDurabilityModifier.class);
                    ItemStack itemStack = player.getInventory().getItemInMainHand();
                    ITCItem itcItem = TCItems.toTCItem(itemStack);
                    if(itcItem instanceof IHasDurability iHasDurability){
                        player.getInventory().setItemInMainHand(iHasDurability.reduceDurability(itemStack, -repairAmount));
                    }
                });
            }
        }.runTaskTimer(TradeCore.getInstance(), 0, 1200);

        //誰もいないダンジョン削除
        new BukkitRunnable() {
            @Override
            public void run() {
                TradeCore.getInstance().getIDungeonService().killEmptyDungeons();
            }
        }.runTaskTimer(TradeCore.getInstance(), 0, 6000);

        //イベントレシーバに1秒が経ったことを伝える
        new BukkitRunnable() {
            @Override
            public void run() {
                TradeCore.getInstance().getEventReciever().onSecondPassed();
            }
        }.runTaskTimer(TradeCore.getInstance(), 0, 20);
    }
}
