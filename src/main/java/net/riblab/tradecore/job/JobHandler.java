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
     * プレイヤーの特定のJobデータを初期化する
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

    /**
     * プレイヤーのJobDataを取得する
     */
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

    /**
     * プレイヤーのjobデータを差し替える
     * @param offlinePlayer
     * @param dataToSet
     */
    public void setJobData(OfflinePlayer offlinePlayer, JobData dataToSet){
        if(dataToSet == null)
            return;
        
        UUID uuid = offlinePlayer.getUniqueId();
        List<JobData> datas = datasMap.get(uuid);
        if (datas == null) {
            datasMap.put(uuid, new ArrayList<>());
            datas = datasMap.get(uuid);
        }

        JobData data = datas.stream().filter(jobData -> jobData.getJobType() == dataToSet.jobType).findAny().orElse(null);
        if (data == null) {
            data = initPlayerJobData(offlinePlayer, dataToSet.jobType);
        }
        
        data.setLevel(dataToSet.getLevel());
        data.setExp(dataToSet.exp);
    }

    /**
     * プレイヤーの全てのjobデータを削除する
     */
    public void resetJobData(OfflinePlayer offlinePlayer){
        UUID uuid = offlinePlayer.getUniqueId();
        List<JobData> datas = datasMap.get(uuid);
        if (datas == null) {
            return;
        }
        
        datas.clear();
    }
}
