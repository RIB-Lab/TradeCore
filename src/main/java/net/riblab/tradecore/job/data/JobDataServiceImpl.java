/*
 * Copyright (c) 2023. RIBLaB
 */
package net.riblab.tradecore.job.data;

import net.kyori.adventure.text.Component;
import net.riblab.tradecore.config.DataService;
import net.riblab.tradecore.general.Utils;
import net.riblab.tradecore.modifier.IJobExpModifier;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.*;

public enum JobDataServiceImpl implements JobDataService {
    INSTANCE;

    private final Map<UUID, List<IJobData>> datasMap = DataService.getImpl().getJobDatas().getPlayerJobs();

    /**
     * プレイヤーの特定のJobデータを初期化する
     */
    private IJobData initPlayerJobData(OfflinePlayer offlinePlayer, JobType type) {
        UUID uuid = offlinePlayer.getUniqueId();
        List<IJobData> datas = datasMap.get(uuid);
        JobData data = new JobData();
        data.jobType = type;
        data.level = 0;
        data.exp = 0;
        datas.add(data);
        return data;
    }

    @Override
    @ParametersAreNonnullByDefault
    public void addJobExp(OfflinePlayer offlinePlayer, JobType type, int amount) {
        UUID uuid = offlinePlayer.getUniqueId();
        List<IJobData> datas = datasMap.get(uuid);
        if (Objects.isNull(datas)) {
            datasMap.put(uuid, new ArrayList<>());
            datas = datasMap.get(uuid);
        }

        IJobData data = datas.stream().filter(jobData -> jobData.getJobType() == type).findAny().orElse(null);
        if (Objects.isNull(data)) {
            data = initPlayerJobData(offlinePlayer, type);
        }

        if (data.getLevel() >= JobData.requiredExp.size()) {
            data.setExp(0);
            if (offlinePlayer instanceof Player player) {
                player.sendExperienceChange(0, data.getLevel());
            }
            return;
        }

        int newExp;
        if (!(offlinePlayer instanceof Player player)) {
            newExp = data.getExp() + amount;
        } else {
            int amountSkillApplied = Utils.apply(player, amount, IJobExpModifier.class);
            newExp = data.getExp() + amountSkillApplied;
        }

        double expRequiredForNextLevel = JobData.requiredExp.get(data.getLevel());
        if (newExp > expRequiredForNextLevel) {
            int newLevel = data.getLevel() + 1;
            data.setLevel(newLevel);
            data.setExp(0);
            if (offlinePlayer instanceof Player player) {
                player.sendMessage(Component.text(type.getName() + "のレベルが" + newLevel + "に上がりました！"));
                player.sendExperienceChange(0, newLevel);
            }
        } else {
            data.setExp(newExp);
            if (offlinePlayer instanceof Player player) {
                player.sendExperienceChange((float) (newExp / expRequiredForNextLevel), data.getLevel());
            }
        }
    }

    @Override
    @ParametersAreNonnullByDefault
    public IJobData getJobData(OfflinePlayer offlinePlayer, JobType type) {
        UUID uuid = offlinePlayer.getUniqueId();
        List<IJobData> datas = datasMap.get(uuid);
        if (Objects.isNull(datas)) {
            datasMap.put(uuid, new ArrayList<>());
            datas = datasMap.get(uuid);
        }

        IJobData data = datas.stream().filter(jobData -> jobData.getJobType() == type).findAny().orElse(null);
        if (Objects.isNull(data)) {
            data = initPlayerJobData(offlinePlayer, type);
        }

        return data;
    }

    @Override
    @ParametersAreNonnullByDefault
    public void setJobData(OfflinePlayer offlinePlayer, JobData dataToSet) {
        UUID uuid = offlinePlayer.getUniqueId();
        List<IJobData> datas = datasMap.get(uuid);
        if (Objects.isNull(datas)) {
            datasMap.put(uuid, new ArrayList<>());
            datas = datasMap.get(uuid);
        }

        IJobData data = datas.stream().filter(jobData -> jobData.getJobType() == dataToSet.jobType).findAny().orElse(null);
        if (Objects.isNull(data)) {
            data = initPlayerJobData(offlinePlayer, dataToSet.jobType);
        }

        data.setLevel(dataToSet.getLevel());
        data.setExp(dataToSet.exp);
    }

    @Override
    public void resetJobData(OfflinePlayer offlinePlayer) {
        UUID uuid = offlinePlayer.getUniqueId();
        List<IJobData> datas = datasMap.get(uuid);
        if (Objects.isNull(datas)) {
            return;
        }

        datas.clear();
    }
}
