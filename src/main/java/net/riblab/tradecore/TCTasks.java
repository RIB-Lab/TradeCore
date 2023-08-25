package net.riblab.tradecore;

import net.kyori.adventure.text.Component;
import net.riblab.tradecore.integration.EconomyImplementer;
import net.riblab.tradecore.item.attribute.IHasDurability;
import net.riblab.tradecore.item.attribute.ITCItem;
import net.riblab.tradecore.item.TCItems;
import net.riblab.tradecore.modifier.IEveryMinuteDurabilityModifier;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * プラグイン起動時から走り続けるBukkitRunnableのタスクたち
 */
public class TCTasks {
    
    private EconomyImplementer getEconomy(){
        return TradeCore.getInstance().getEconomy();
    }
    
    public TCTasks(){
        //所持金と投票券表示
        new BukkitRunnable() {
            @Override
            public void run() {
                String negativeSpace = TCResourcePackData.IconsFont.NEGATIVE_SPACE.get_char();
                Bukkit.getOnlinePlayers().forEach(player -> {
                    int balance = (int) getEconomy().getBalance(player);
                    int tickets = getEconomy().getPlayTickets(player);
                    Component text = Component.text("");
                    text = text.append(Component.text(negativeSpace + negativeSpace + negativeSpace + negativeSpace + TCResourcePackData.IconsFont.COIN.get_char()).font(TCResourcePackData.iconsFontName));
                    text = text.append(Component.text(" " + balance).font(TCResourcePackData.yPlus12FontName));
                    text = text.append(Component.text("                         " + TCResourcePackData.IconsFont.VOTE_TICKET.get_char()).font(TCResourcePackData.iconsFontName));
                    text = text.append(Component.text(" " + tickets).font(TCResourcePackData.yPlus12FontName));
                    player.sendActionBar(text);
                });
            }
        }.runTaskTimer(TradeCore.getInstance(), 0, 20);

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
                TradeCore.getInstance().getDungeonService().killEmptyDungeons();
            }
        }.runTaskTimer(TradeCore.getInstance(), 0, 6000);
    }
}
