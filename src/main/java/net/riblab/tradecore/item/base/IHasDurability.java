package net.riblab.tradecore.item.base;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.riblab.tradecore.general.NBTTagNames;
import net.riblab.tradecore.item.ItemCreator;
import net.riblab.tradecore.item.mod.IItemMod;
import net.riblab.tradecore.modifier.IDurabilityModifier;
import org.bukkit.inventory.ItemStack;

import java.util.List;

/**
 * 耐久値を持つアイテム
 */
public interface IHasDurability extends ITCItem {

    /**
     * ツールの基礎最大耐久値。-1で無限
     */
    DurabilityTable getDurabilityTable();

    /**
     * ツールのインスタンスの耐久値を減らす / 回復させる
     */
    default ItemStack reduceDurability(ItemStack instance, int amount){
        if (!isSimilar(instance))
            return null;

        int durability = new ItemCreator(instance).getIntNBT(NBTTagNames.DURABILITY.get());

        if (durability == -1) //耐久無限
            return instance;

        durability -= amount;

        if (durability <= 0) //耐久切れ
            return null;

        int maxDurability = getMaxDurability(instance);

        if (durability > maxDurability) //耐久MAX
            durability = maxDurability;

        int damageToSet = (int) (instance.getType().getMaxDurability() * ((float) durability / (float) maxDurability));
        int damageToDeal = (instance.getType().getMaxDurability() - instance.getDurability()) - damageToSet;
        return new ItemCreator(instance).setLores(getLore(durability, new ItemCreator(instance).getItemMods())).damage(damageToDeal).setIntNBT(NBTTagNames.DURABILITY.get(), durability).create();
    }


    /**
     * ItemStackに付与されたmodから最大耐久値を割り出す
     */
    default int getMaxDurability(ItemStack instance){
        return getMaxDurability(new ItemCreator(instance).getItemMods());
    }

    /**
     * mod達の中から最大耐久値を持つmodを割出し、その値を返す
     */
    default int getMaxDurability(List<IItemMod<?>> mods){
        IItemMod<?> maxDurabilityMod = mods.stream().filter(iItemMod -> iItemMod instanceof IDurabilityModifier).findFirst().orElse(null);
        return maxDurabilityMod != null ? (int)maxDurabilityMod.getParam() : getDurabilityTable().getMiddleMaxDurability(); //アイテムにランダムな最大耐久値が付与されていなかったらフォールバックとして基礎最大耐久値を使う
    }

    /**
     * 耐久値のツールチップを取得する
     */
    default TextComponent getDurabilityLore(int durability, List<IItemMod<?>> randomMods){
        int maxDurability = getMaxDurability(randomMods);

        return Component.text("耐久値: ").decoration(TextDecoration.ITALIC, false).color(NamedTextColor.WHITE)
                .append(Component.text(durability).color(durability == maxDurability ? NamedTextColor.WHITE : NamedTextColor.YELLOW))
                .append(Component.text("/" + maxDurability).color(NamedTextColor.WHITE));
    }
}
