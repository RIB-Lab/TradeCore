package net.riblab.tradecore.craft;

import lombok.Data;
import net.riblab.tradecore.item.ITCItem;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

/**
 * 精錬レシピクラス
 */
@Data
public class TCFurnaceRecipe {
    /**
     * レシピ素材。同じ種類のアイテムを複数スロットに入れないこと！
     */
    private final Map<ITCItem, Integer> ingredients;

    /**
     * レシピ完成品
     */
    private final ItemStack result;

    /**
     * 完成品の量
     */
    private final int resultAmount;

    /**
     * レシピを実行するために消費する燃料の量
     */
    private final int fuelAmount;
}