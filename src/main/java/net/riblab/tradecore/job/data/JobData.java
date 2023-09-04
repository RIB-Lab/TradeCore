package net.riblab.tradecore.job.data;

import lombok.Data;

/**
 * プレイヤーが配列で持つジョブ一つ分のデータ
 */
@Data
public class JobData implements IJobData {

    /**
     * このデータが保存する職業の種類
     */
    JobType jobType;

    /**
     * 現在のレベル
     */
    int level;

    /**
     * 現在の経験値
     */
    int exp;
}
