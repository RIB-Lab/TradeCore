/*
 * Copyright (c) 2023. RIBLaB 
 */
package net.riblab.tradecore.modifier;

import lombok.Data;
import net.riblab.tradecore.craft.ITCCraftingRecipe;

/**
 * あるレシピの材料の数を変更するスキル
 */
public interface IIngredientAmountModifier extends IModifier<IIngredientAmountModifier.PackedRecipeData> {

    @Data
    class PackedRecipeData {
        ITCCraftingRecipe recipe;
        int amount;
    }
}
