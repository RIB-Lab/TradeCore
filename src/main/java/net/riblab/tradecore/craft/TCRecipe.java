package net.riblab.tradecore.craft;

import lombok.Data;
import net.riblab.tradecore.UICraftingTable;
import net.riblab.tradecore.item.ITCItem;

import java.util.Map;

/**
 * レシピクラス
 */
@Data
public class TCRecipe {
    /**
     * レシピ素材。同じ種類のアイテムを複数スロットに入れないこと！
     */
    private final Map<ITCItem, Integer> ingredients;

    /**
     * レシピ完成品
     */
    private final ITCItem result;

    /**
     * 完成品の量
     */
    private final int resultAmount;

    /**
     * レシピを実行するための費用
     */
    private final int fee;

    /**
     * レシピのカテゴリ
     */
    private final TCRecipes.RecipeType category;
}
