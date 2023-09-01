package net.riblab.tradecore.craft;

import net.riblab.tradecore.item.base.ITCItem;

import java.util.Map;

/**
 * レシピクラス
 *
 * @param ingredients  レシピ素材。同じ種類のアイテムを複数スロットに入れないこと！
 * @param result       レシピ完成品
 * @param resultAmount 完成品の量
 * @param fee          レシピを実行するための費用
 * @param category     レシピのカテゴリ
 */
record TCCraftingRecipe(Map<ITCItem, Integer> ingredients, ITCItem result, int resultAmount, double fee,
                        TCCraftingRecipes.RecipeType category) implements ITCCraftingRecipe {
}
