package net.riblab.tradecore.job.skill;

import net.riblab.tradecore.craft.TCCraftingRecipe;
import net.riblab.tradecore.craft.TCCraftingRecipes;
import net.riblab.tradecore.job.JobData;
import net.riblab.tradecore.job.JobSkill;

import java.util.List;
import java.util.Set;

public class JSFreeStick extends JobSkill implements ICraftFeeModifier {

    public static final String name = "棒系レシピのクラフトコストが無料に(要2ポイント)";
    public static final List<JobData.JobType> availableSkillType = List.of(JobData.JobType.Woodcutter);

    private static final Set<TCCraftingRecipe> appliedRecipes = Set.of(TCCraftingRecipes.STICK.getRecipe(), TCCraftingRecipes.REINFORCED_STICK.getRecipe());
    
    @Override
    public PackedCraftFee apply(PackedCraftFee originalFee, PackedCraftFee modifiedFee) {
        PackedCraftFee newFee = new PackedCraftFee();
        newFee.setRecipe(modifiedFee.getRecipe());
        if(appliedRecipes.contains(modifiedFee.getRecipe()) && getLevel() >= 2){
            newFee.setFee(0);
        }
        else{
            newFee.setFee(modifiedFee.getFee());
        }
        return newFee;
    }
}
