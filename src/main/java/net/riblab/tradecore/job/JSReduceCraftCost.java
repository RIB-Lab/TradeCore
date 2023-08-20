package net.riblab.tradecore.job;

import java.util.List;

public class JSReduceCraftCost extends JobSkill implements ICraftCostModifier{

    public static final String name = "クラフトの消費金額削減";
    public static final List<JobData.JobType> availableSkillType = List.of(JobData.JobType.Crafter);

    @Override
    public Double apply(Double originalPrice) {
        return originalPrice * (1 - getLevel() * 0.05);
    }
}
