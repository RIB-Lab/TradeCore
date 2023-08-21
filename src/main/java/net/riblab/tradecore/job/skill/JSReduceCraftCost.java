package net.riblab.tradecore.job.skill;

import net.riblab.tradecore.job.JobData;
import net.riblab.tradecore.job.JobSkill;

import java.util.List;

public class JSReduceCraftCost extends JobSkill implements ICraftFeeModifier {

    public static final String name = "クラフトの消費金額削減";
    public static final List<JobData.JobType> availableSkillType = List.of(JobData.JobType.Crafter);

    @Override
    public PackedCraftFee apply(PackedCraftFee originalFee, PackedCraftFee modifiedFee) {
        PackedCraftFee newFee = new PackedCraftFee();
        newFee.setRecipe(modifiedFee.getRecipe());
        newFee.setFee(modifiedFee.getFee() * (1 - getLevel() * 0.05));
        return newFee;
    }
}
