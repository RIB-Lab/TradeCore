package net.riblab.tradecore.job;

import org.bukkit.OfflinePlayer;

public interface JobDataService {
    /**
     * プレイヤーの特定のJobデータを初期化する
     */
    IJobData initPlayerJobData(OfflinePlayer offlinePlayer, JobData.JobType type);

    /**
     * プレイヤーにJob経験値を加える
     */
    void addJobExp(OfflinePlayer offlinePlayer, JobData.JobType type, int amount);

    /**
     * プレイヤーのJobDataを取得する
     */
    IJobData getJobData(OfflinePlayer offlinePlayer, JobData.JobType type);

    /**
     * プレイヤーのjobデータを差し替える
     *
     * @param offlinePlayer
     * @param dataToSet
     */
    void setJobData(OfflinePlayer offlinePlayer, JobData dataToSet);

    /**
     * プレイヤーの全てのjobデータを削除する
     */
    void resetJobData(OfflinePlayer offlinePlayer);
}
