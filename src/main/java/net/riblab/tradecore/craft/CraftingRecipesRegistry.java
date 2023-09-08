/*
 * Copyright (c) 2023. RIBLaB
 */
package net.riblab.tradecore.craft;

import net.riblab.tradecore.item.base.TCItemRegistry;

import javax.annotation.Nonnull;
import java.util.*;

public enum CraftingRecipesRegistry {
    INSTANCE;

    /**
     * デシリアライズしたクラフトレシピ
     */
    private final Map<String, ITCCraftingRecipe> deserializedCraftingRecipes = new HashMap<>();

    /**
     * ある種類のレシピを全て取得する
     */
    @Nonnull
    public Map<String, ITCCraftingRecipe> getRecipes(RecipeType type) {
        Map<String, ITCCraftingRecipe> result = new HashMap<>();
        deserializedCraftingRecipes.forEach((s, recipe) -> {
            if (recipe.getCategory() == type) {
                result.put(s, recipe);
            }
        });
        return result;
    }

    /**
     * レシピの種類
     */
    public enum RecipeType {
        ARMOR, TOOL, WEAPON, MISC
    }

    /**
     * 編集不可能なレシピのコピーを取得する
     */
    public Map<String, ITCCraftingRecipe> getRecipes() {
        return Collections.unmodifiableMap(deserializedCraftingRecipes);
    }

    public void clear() {
        deserializedCraftingRecipes.clear();
    }

    public void addAll(Map<String, ITCCraftingRecipe> recipes) {
        deserializedCraftingRecipes.putAll(recipes);
    }

    /**
     * クラフトレシピがちゃんと成立しているか確認する
     */
    public void validate() {
        for (Map.Entry<String, ITCCraftingRecipe> recipeEntry : deserializedCraftingRecipes.entrySet()) {
            TCItemRegistry.INSTANCE.commandToTCItem(recipeEntry.getValue().getResult()).orElseThrow(
                    () -> new NoSuchElementException("クラフトレシピ" + recipeEntry.getKey() + "の成果物" + recipeEntry.getValue().getResult() + "がアイテムレジストリに見つかりません。"));
            recipeEntry.getValue().getIngredients().forEach((String ingredient, Integer amount) -> {
                TCItemRegistry.INSTANCE.commandToTCItem(ingredient).orElseThrow(
                        () -> new NoSuchElementException("クラフトレシピ" + recipeEntry.getKey() + "の材料" + ingredient + "がアイテムレジストリに見つかりません。"));
            });
            if (recipeEntry.getValue().getFee() < 0 || recipeEntry.getValue().getResultAmount() < 0) {
                throw new IllegalStateException("クラフトレシピ" + recipeEntry.getKey() + "の数値が不正です");
            }
        }
    }
}
