/*
 * Copyright (c) 2023. RIBLaB
 */
package net.riblab.tradecore.craft;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public enum CraftingRecipesRegistry {
    INSTANCE;

    /**
     * デシリアライズしたアイテム
     */
    private final List<ITCCraftingRecipe> deserializedCraftingRecipes = new ArrayList<>();

    /**
     * ある種類のレシピを全て取得する
     */
    @Nonnull
    public List<ITCCraftingRecipe> getRecipes(RecipeType type) {
        return deserializedCraftingRecipes.stream().filter(tcCraftingRecipe -> tcCraftingRecipe.getCategory() == type).collect(Collectors.toList());
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
    public Collection<ITCCraftingRecipe> getRecipes() {
        return List.copyOf(deserializedCraftingRecipes);
    }

    public void clear() {
        deserializedCraftingRecipes.clear();
    }

    public void addAll(List<ITCCraftingRecipe> recipes) {
        deserializedCraftingRecipes.addAll(recipes);
    }
}
