/*
 * Copyright (c) 2023. RIBLaB
 */
package net.riblab.tradecore.job.skill;

import net.kyori.adventure.text.Component;
import net.riblab.tradecore.job.data.JobType;

import java.util.List;

public interface IJobSkill {
    /**
     * スキルの名前。必ず子クラスで定義する！
     */
    String name = "ダミー";
    /**
     * このスキルを習得可能なJobのリスト。必ず子クラスで定義する！
     */
    List<JobType> availableSkillType = List.of(JobType.MINER);
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

    JobType getLearnedJobType();

    void setInternalName(String internalName);

    void setLevel(int level);

    void setLearnedJobType(JobType learnedJobType);
}
