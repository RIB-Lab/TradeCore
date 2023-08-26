package net.riblab.tradecore.craft;

import lombok.Data;
import net.riblab.tradecore.item.attribute.ITCItem;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

/**
 * レシピクラス
 */
@Data
public class TCCraftingRecipe implements ITCCraftingRecipe {
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
     * レシピを実行するための費用
     */
    private final double fee;

    /**
     * レシピのカテゴリ
     */
    private final TCCraftingRecipes.RecipeType category;
}
