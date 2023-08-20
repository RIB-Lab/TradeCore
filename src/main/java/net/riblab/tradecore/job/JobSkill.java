package net.riblab.tradecore.job;

import de.exlll.configlib.Configuration;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Configuration
public class JobSkill {

    /**
     * スキルの名前。必ず子クラスで定義する！
     */
    public static final String name = "ダミー";
    
    /**
     * このスキルを習得可能なJobのリスト。必ず子クラスで定義する！
     */
    public static final List<JobData.JobType> availableSkillType = List.of(JobData.JobType.Miner);

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
    private JobData.JobType learnedJobType;
}
