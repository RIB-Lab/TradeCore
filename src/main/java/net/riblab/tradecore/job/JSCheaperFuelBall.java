package net.riblab.tradecore.job;

import net.riblab.tradecore.craft.TCCraftingRecipes;

import java.util.List;

/**
 * 燃料玉レシピのコストを安くするスキル
 */
public class JSCheaperFuelBall extends JobSkill implements IIngredientAmountModifier {

    public static final String name = "燃料玉の材料削減";
    public static final List<JobData.JobType> availableSkillType = List.of(JobData.JobType.Crafter);

    @Override
    public PackedRecipeData apply(PackedRecipeData originalValue, PackedRecipeData modifiedValue) {
        if(!originalValue.getRecipe().equals(TCCraftingRecipes.FUEL_BALL.getRecipe()))
            return modifiedValue;
        
        int newAmount = modifiedValue.getAmount() - getLevel();
        newAmount = Math.max(newAmount, 1);
        modifiedValue.setAmount(newAmount);
        return modifiedValue;
    }
}
