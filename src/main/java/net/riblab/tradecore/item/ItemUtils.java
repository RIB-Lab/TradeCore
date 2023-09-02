package net.riblab.tradecore.item;

import com.google.common.collect.Multimap;
import net.riblab.tradecore.general.Utils;
import net.riblab.tradecore.item.base.ITCItem;
import net.riblab.tradecore.item.base.PackedDurabilityData;
import net.riblab.tradecore.item.base.TCItems;
import net.riblab.tradecore.item.mod.IItemMod;
import net.riblab.tradecore.item.mod.ModRandomDurabilityI;
import net.riblab.tradecore.modifier.IResourceChanceModifier;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
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

    /**
     * ツールのインスタンスの耐久値を減らす / 回復させる。ツールに耐久値modがついていない場合、耐久値無限とみなし何もしない
     */
    public static ItemStack reduceDurabilityIfPossible(ItemStack instance, int amount){
        ITCItem itcItem = TCItems.toTCItem(instance);
        if(itcItem == null)
            return instance;
        
        PackedDurabilityData durs = getDurability(instance);

        if (durs.getCurrentDur() == -1) //耐久無限
            return instance;

        durs.setCurrentDur(durs.getCurrentDur() - amount);

        if (durs.getCurrentDur() <= 0) //耐久切れ
            return null;

        if (durs.getCurrentDur() > durs.getMaxDur()) //耐久MAX
            durs.setCurrentDur(durs.getMaxDur());

        int damageToSet = (int) (instance.getType().getMaxDurability() * ((float) durs.getCurrentDur() / (float) durs.getMaxDur()));
        int damageToDeal = (instance.getType().getMaxDurability() - instance.getDurability()) - damageToSet;
        return new ItemCreator(instance).setLores(itcItem.getLore(new ItemCreator(instance).getItemMods())).writeItemMod(new ModRandomDurabilityI(durs)).damage(damageToDeal).create();
    }


    /**
     * ItemStackに付与されたmodから耐久値と最大耐久値を割り出す
     */
    public static PackedDurabilityData getDurability(ItemStack instance){
        return getDurability(new ItemCreator(instance).getItemMods());
    }

    /**
     * mod達の中から耐久値と最大耐久値を持つmodを割出し、その値を返す(modがなかったら耐久無限として扱う)
     */
    public static PackedDurabilityData getDurability(List<IItemMod<?>> mods){
        ModRandomDurabilityI maxDurabilityMod = (ModRandomDurabilityI) mods.stream().filter(iItemMod -> iItemMod instanceof ModRandomDurabilityI).findFirst().orElse(null);
        return maxDurabilityMod != null ? maxDurabilityMod.getParam() : new PackedDurabilityData(-1,-1);
    }
}
