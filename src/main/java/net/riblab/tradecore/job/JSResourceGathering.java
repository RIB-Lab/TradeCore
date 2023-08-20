package net.riblab.tradecore.job;

import java.util.List;

public class JSResourceGathering extends JobSkill implements IResourceChanceModifier {

    public static final String name = "資源採取効率強化";
    public static final List<JobData.JobType> availableSkillType = List.of(JobData.JobType.Miner, JobData.JobType.Woodcutter, JobData.JobType.Digger);

    @Override
    public Float apply(Float originalChance, Float modifiedChance) {
        return modifiedChance + originalChance * (getLevel() * 0.01f);
    }
}
