package net.riblab.tradecore.config;

import javax.annotation.Nullable;
import java.io.File;

public interface ConfigService {

    static ConfigService getImpl(File dataFolder) {
        return new ConfigServiceImpl(dataFolder);
    }

    /**
     * コンフィグを保存する
     */
    void save();

    /**
     * コンフィグをロードする
     */
    void load();

    @Nullable
    CurrencyData getCurrencyData();

    @Nullable
    JobDatas getJobDatas();
}
