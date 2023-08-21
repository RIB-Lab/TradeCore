package net.riblab.tradecore.job.skill;

import lombok.Data;
import net.riblab.tradecore.craft.TCCraftingRecipe;

/**
 * あるレシピの材料の数を変更するスキル
 */
public interface IIngredientAmountModifier extends IModifier<IIngredientAmountModifier.PackedRecipeData> {
    
    @Data
    public class PackedRecipeData{
        TCCraftingRecipe recipe;
        int amount;
    }
}
