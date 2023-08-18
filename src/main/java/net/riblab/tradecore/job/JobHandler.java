package net.riblab.tradecore.job;

import net.kyori.adventure.text.Component;
import net.riblab.tradecore.TradeCore;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class JobHandler {

    private final Map<UUID, List<JobData>> datasMap = TradeCore.getInstance().getConfigManager().getJobDatas().playerJobs;

    /**
     * プレイヤーのJobデータを初期化する
     *
     * @param offlinePlayer
     * @param type
     */
    public JobData initPlayerJobData(OfflinePlayer offlinePlayer, JobData.JobType type) {
        UUID uuid = offlinePlayer.getUniqueId();
        List<JobData> datas = datasMap.get(uuid);
        JobData data = new JobData();
        data.jobType = type;
        data.level = 0;
        data.exp = 0;
        datas.add(data);
        return data;
    }

    /**
     * プレイヤーにJob経験値を加える
     *
     * @param offlinePlayer
     * @param type
     * @param amount
     */
    public void addJobExp(OfflinePlayer offlinePlayer, JobData.JobType type, int amount) {
        UUID uuid = offlinePlayer.getUniqueId();
        List<JobData> datas = datasMap.get(uuid);
        if (datas == null) {
            datasMap.put(uuid, new ArrayList<>());
            datas = datasMap.get(uuid);
        }

        JobData data = datas.stream().filter(jobData -> jobData.getJobType() == type).findAny().orElse(null);
        if (data == null) {
            data = initPlayerJobData(offlinePlayer, type);
        }

        if (data.getLevel() > JobData.requiredExp.size()) {
            data.setExp(0);
            return;
        }

        int newExp = data.getExp() + amount;
        double expRequiredForNextLevel = JobData.requiredExp.get(data.getLevel());
        if (newExp > expRequiredForNextLevel) {
            int newLevel = data.getLevel() + 1;
            data.setLevel(newLevel);
            data.setExp(0);
            if (offlinePlayer instanceof Player)
                ((Player) offlinePlayer).sendMessage(Component.text(type.getName() + "のレベルが" + newLevel + "に上がりました！"));
        } else {
            data.setExp(newExp);
        }
    }

    public JobData getJobData(OfflinePlayer offlinePlayer, JobData.JobType type) {
        UUID uuid = offlinePlayer.getUniqueId();
        List<JobData> datas = datasMap.get(uuid);
        if (datas == null) {
            datasMap.put(uuid, new ArrayList<>());
            datas = datasMap.get(uuid);
        }

        JobData data = datas.stream().filter(jobData -> jobData.getJobType() == type).findAny().orElse(null);
        if (data == null) {
            data = initPlayerJobData(offlinePlayer, type);
        }

        return data;
    }
}
