package net.riblab.tradecore.job;

import net.kyori.adventure.text.Component;

import java.util.List;

public interface IJobSkill {
    /**
     * スキルの名前。必ず子クラスで定義する！
     */
    String name = "ダミー";
    /**
     * このスキルを習得可能なJobのリスト。必ず子クラスで定義する！
     */
    List<JobData.JobType> availableSkillType = List.of(JobData.JobType.Miner);
    /**
     * スキルの紹介文
     */
    List<Component> lore = List.of(Component.text("ダミー"));
    /**
     * スキルの最大レベル
     */
    int maxLevel = 1;

    String getInternalName();

    int getLevel();

    JobData.JobType getLearnedJobType();

    void setInternalName(String internalName);

    void setLevel(int level);

    void setLearnedJobType(JobData.JobType learnedJobType);
}
