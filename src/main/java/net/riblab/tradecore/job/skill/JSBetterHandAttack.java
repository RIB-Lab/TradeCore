package net.riblab.tradecore.job.skill;

import net.riblab.tradecore.job.JobData;
import net.riblab.tradecore.job.JobSkill;

import java.util.List;

public class JSBetterHandAttack extends JobSkill implements IHandAttackDamageModifier {

    public static final String name = "殴りダメージアップ";
    public static final List<JobData.JobType> availableSkillType = List.of(JobData.JobType.Mower);

    @Override
    public Double apply(Double originalValue, Double modifiedValue) {
        modifiedValue += getLevel() * 0.5d;
        return modifiedValue;
    }
}
