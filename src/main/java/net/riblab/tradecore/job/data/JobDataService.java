package net.riblab.tradecore.job.data;

import org.bukkit.OfflinePlayer;

import javax.annotation.ParametersAreNonnullByDefault;

public interface JobDataService {

    static JobDataService getImpl() {
        return JobDataServiceImpl.INSTANCE;
    }

    /**
     * プレイヤーにJob経験値を加える
     */
    @ParametersAreNonnullByDefault
    void addJobExp(OfflinePlayer offlinePlayer, JobType type, int amount);

    /**
     * プレイヤーのJobDataを取得する
     */
    @ParametersAreNonnullByDefault
    IJobData getJobData(OfflinePlayer offlinePlayer, JobType type);

    /**
     * プレイヤーのjobデータを差し替える
     */
    @ParametersAreNonnullByDefault
    void setJobData(OfflinePlayer offlinePlayer, JobData dataToSet);

    /**
     * プレイヤーの全てのjobデータを削除する
     */
    @ParametersAreNonnullByDefault
    void resetJobData(OfflinePlayer offlinePlayer);
}
