package net.riblab.tradecore.job;

import java.util.List;

public class JSResourceGathering extends JobSkill implements IResourceChanceModifier {

    public static final String name = "資源採取";
    public static final List<JobData.JobType> availableSkillType = List.of(JobData.JobType.Miner, JobData.JobType.Woodcutter, JobData.JobType.Digger);

    @Override
    public double apply(double originalChance) {
        return originalChance * (1 + getLevel() * 0.1);
    }
}