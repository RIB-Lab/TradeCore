package net.riblab.tradecore.job.skill;

import net.riblab.tradecore.craft.TCCraftingRecipes;
import net.riblab.tradecore.job.JobData;
import net.riblab.tradecore.job.JobSkill;

import java.util.List;
import java.util.Random;

public class JSBetterTraining extends JobSkill implements IJobExpModifier {

    public static final String name = "ジョブ経験値獲得量アップ";
    public static final List<JobData.JobType> availableSkillType = List.of(JobData.JobType.Crafter);

    @Override
    public Integer apply(Integer originalValue, Integer modifiedValue) {
        float chance = new Random().nextFloat();
        if(chance < (getLevel() * 0.2)){
            return modifiedValue + 1;
        }
        return modifiedValue;
    }
}
