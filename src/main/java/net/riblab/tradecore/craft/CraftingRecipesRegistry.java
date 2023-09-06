package net.riblab.tradecore.craft;

import lombok.Getter;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public enum CraftingRecipesRegistry {
    INSTANCE;

    /**
     * デシリアライズしたアイテム
     */
    @Getter
    private final List<ITCCraftingRecipe> deserializedCraftingRecipes = new ArrayList<>();//TODO:ゲッターを削除して読み書きをメソッド経由で行うように

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
}
