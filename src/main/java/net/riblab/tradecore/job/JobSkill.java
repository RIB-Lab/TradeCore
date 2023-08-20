package net.riblab.tradecore.job;

import de.exlll.configlib.Configuration;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.List;

@Configuration
public abstract class JobSkill {
    
    public static final String name = "ダミー";
    
    /**
     * このスキルを習得可能なJobのリスト
     */
    public static final List<JobData.JobType> availableSkillType = List.of(JobData.JobType.Miner);
    
    /**
     * 現在習得したこのスキルのレベル
     */
    @Getter @Setter
    private int level;
    
    /**
     * 現在このスキルをどのジョブで習得したか
     */
    @Getter @Setter
    private JobData.JobType learnedSkillType;
    
    public int getMaxLevel(){
        return 1;
    }
}
