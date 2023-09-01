package net.riblab.tradecore.item;

import com.google.common.collect.Multimap;
import net.riblab.tradecore.general.Utils;
import net.riblab.tradecore.item.base.ITCItem;
import net.riblab.tradecore.modifier.IResourceChanceModifier;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Random;

/**
 * アイテム関係のユーティリティクラス
 */
public final class ItemUtils {
    /**
     * ルートテーブルに基づいたアイテムをあるブロックのある場所から落とす
     */
    @ParametersAreNonnullByDefault
    public static void dropItemByLootTable(Player player, Block block, Multimap<Float, ITCItem> table) {
        Random random = new Random();
        table.forEach((aFloat, itcItem) -> {
            float skillAppliedChance = Utils.apply(player, aFloat, IResourceChanceModifier.class);
            float rand = random.nextFloat();
            if (rand < skillAppliedChance) {
                block.getWorld().dropItemNaturally(block.getLocation(), itcItem.getTemplateItemStack());
            }
        });
    }

    /**
     * インベントリ内にITCItemが指定個数個以上あるか
     * @param inventory インベントリ
     * @param itcItem 探したいアイテムの種類
     * @param amount アイテムの量
     */
    public static boolean tcContainsAtLeast(Inventory inventory, ITCItem itcItem, int amount){
        int amountInInv = 0;
        for (ItemStack content : inventory.getContents()) {
            if(itcItem.isSimilar(content)){
                amountInInv += content.getAmount();

                if(amountInInv >= amount)
                    return true; 
            }
        }
        
        return false;
    }

    /**
     * インベントリからTCItemを指定個数除去。これを使うことでItemの細かいNBTが異なっていても同じTCItemとして除去できる
     */
    public static void tcRemoveItemAnySlot(Inventory inventory, ITCItem itcItem, int amount){
        for (ItemStack content : inventory.getContents()) {
            if(itcItem.isSimilar(content)){
                int amountToRemove = Math.min(content.getAmount(), amount);
                content.setAmount(content.getAmount() - amountToRemove);
                amount -= amountToRemove;
                if(amount <= 0)
                    return;
            }
        }
    }
}
