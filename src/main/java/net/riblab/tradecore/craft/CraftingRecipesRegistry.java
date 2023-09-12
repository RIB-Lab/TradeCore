/*
 * Copyright (c) 2023. RIBLaB
 */
package net.riblab.tradecore.craft;

import net.riblab.tradecore.general.IRegistry;
import net.riblab.tradecore.item.base.TCItemRegistry;

import javax.annotation.Nonnull;
import java.util.*;

/**
 * クラフトレシピの実体を保持するクラス
 */
public enum CraftingRecipesRegistry implements IRegistry<Map<String, ITCCraftingRecipe>> {
    INSTANCE;

    /**
     * デシリアライズしたクラフトレシピ
     */
    private final Map<String, ITCCraftingRecipe> deserializedCraftingRecipes = new LinkedHashMap<>(); //作業台で順番通りにレシピを表示するのに必要

    /**
     * ある種類のレシピを全て取得する
     */
    @Nonnull
    public Map<String, ITCCraftingRecipe> getUnmodifiableElements(RecipeType type) {
        Map<String, ITCCraftingRecipe> result = new LinkedHashMap<>();
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
    @Override
    public Map<String, ITCCraftingRecipe> getUnmodifiableElements() {
        return Collections.unmodifiableMap(deserializedCraftingRecipes);
    }

    @Override
    public void clear() {
        deserializedCraftingRecipes.clear();
    }

    @Override
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
