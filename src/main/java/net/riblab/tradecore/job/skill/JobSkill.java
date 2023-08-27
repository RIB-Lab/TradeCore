package net.riblab.tradecore.job.skill;

import de.exlll.configlib.Configuration;
import lombok.Getter;
import lombok.Setter;
import net.riblab.tradecore.job.data.JobData;
import net.riblab.tradecore.job.data.JobType;

@Configuration
public class JobSkill implements IJobSkill {

    /**
     * JobSkillのインスタンスのクラス名
     */
    @Getter @Setter
    private String internalName;
    
    /**
     * 現在習得したこのスキルのレベル
     */
    @Getter @Setter
    private int level;
    
    /**
     * 現在このスキルをどのジョブで習得したか
     */
    @Getter @Setter
    private JobType learnedJobType;
}
