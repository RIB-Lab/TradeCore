package net.riblab.tradecore.item.base;

import net.riblab.tradecore.item.ItemCreator;
import net.riblab.tradecore.item.mod.IItemMod;
import net.riblab.tradecore.item.mod.ModRandomDurabilityI;
import org.bukkit.inventory.ItemStack;

import java.util.List;

/**
 * 耐久値を持つアイテム
 */
public interface IHasDurability extends ITCItem {

    /**
     * ツールのインスタンスの耐久値を減らす / 回復させる
     */
    default ItemStack reduceDurability(ItemStack instance, int amount){
        if (!isSimilar(instance))
            return null;

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
        return new ItemCreator(instance).setLores(getLore(new ItemCreator(instance).getItemMods())).writeItemMod(new ModRandomDurabilityI(durs)).damage(damageToDeal).create();
    }


    /**
     * ItemStackに付与されたmodから耐久値と最大耐久値を割り出す
     */
    default PackedDurabilityData getDurability(ItemStack instance){
        return getDurability(new ItemCreator(instance).getItemMods());
    }

    /**
     * mod達の中から耐久値と最大耐久値を持つmodを割出し、その値を返す
     */
    default PackedDurabilityData getDurability(List<IItemMod<?>> mods){
        ModRandomDurabilityI maxDurabilityMod = (ModRandomDurabilityI) mods.stream().filter(iItemMod -> iItemMod instanceof ModRandomDurabilityI).findFirst().orElse(null);
        return maxDurabilityMod != null ? maxDurabilityMod.getParam() : new PackedDurabilityData(0,0);
    }
}
