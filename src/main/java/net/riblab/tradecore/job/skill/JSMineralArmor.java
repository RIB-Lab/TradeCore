package net.riblab.tradecore.job.skill;

import net.riblab.tradecore.job.JobData;
import net.riblab.tradecore.job.JobSkill;

import java.util.List;

public class JSMineralArmor extends JobSkill implements IArmorModifier {

    public static final String name = "アーマー値が上昇する";
    public static final List<JobData.JobType> availableSkillType = List.of(JobData.JobType.Miner);

    @Override
    public Double apply(Double originalValue, Double modifiedValue) {
        return modifiedValue + originalValue * (getLevel() * 0.1);
    }
}
