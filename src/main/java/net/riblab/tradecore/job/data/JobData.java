package net.riblab.tradecore.job.data;

import de.exlll.configlib.Configuration;
import lombok.Data;

/**
 * プレイヤーが配列で持つジョブ一つ分のデータ
 */
@Data
@Configuration
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