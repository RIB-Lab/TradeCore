/*
 * Copyright (c) 2023. RIBLaB 
 */
package net.riblab.tradecore.config;

import net.riblab.tradecore.job.data.IJobData;
import net.riblab.tradecore.job.data.JobData;
import net.riblab.tradecore.job.skill.IJobSkill;
import net.riblab.tradecore.job.skill.JobSkill;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class JobDatas {
    
    private Map<UUID, List<JobData>> playerJobs = new HashMap<>();
    
    private Map<UUID, List<JobSkill>> playerJobSkills = new HashMap<>();

    /**
     * プレイヤーのJobを操作するときはインターフェース経由で
     */
    @SuppressWarnings("unchecked")
    public Map<UUID, List<IJobData>> getPlayerJobs() {
        return (Map<UUID, List<IJobData>>) (Object) playerJobs;
    }

    /**
     * プレイヤーのJobスキルを操作するときはインターフェース経由で
     */
    @SuppressWarnings("unchecked")
    public Map<UUID, List<IJobSkill>> getPlayerJobSkills() {
        return (Map<UUID, List<IJobSkill>>) (Object) playerJobSkills;
    }
}
