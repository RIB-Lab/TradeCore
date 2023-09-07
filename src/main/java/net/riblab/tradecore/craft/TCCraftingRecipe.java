/*
 * Copyright (c) 2023. RIBLaB
 */
package net.riblab.tradecore.craft;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

/**
 * レシピクラス。コンフィグから読み込むときだけ新しいインスタンスを建てる
 */
public final class TCCraftingRecipe implements ITCCraftingRecipe {
    @Getter
    @Setter
    private Map<String, Integer> ingredients;
    @Getter
    @Setter
    private String result;
    @Getter
    @Setter
    private int resultAmount;
    @Getter
    @Setter
    private double fee;
    @Getter
    @Setter
    private CraftingRecipesRegistry.RecipeType category;
    @Getter
    @Setter
    private String internalName;

    public TCCraftingRecipe() {

    }
}
