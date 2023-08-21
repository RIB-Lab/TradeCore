package net.riblab.tradecore.job.skill;

import net.riblab.tradecore.job.JobData;
import net.riblab.tradecore.job.JobSkill;

import java.util.List;

/**
 * ツールで敵を殴れるようになる
 */
public class JSHeavyLifting extends JobSkill implements ICanHitWithToolModifier {

    public static final String name = "ツールで敵を殴れるようになる(要3ポイント)";
    public static final List<JobData.JobType> availableSkillType = List.of(JobData.JobType.Digger);
    
    @Override
    public Boolean apply(Boolean originalValue, Boolean modifiedValue) {
        if(getLevel() >= 3)
            return true;
        else return modifiedValue;
    }
}
