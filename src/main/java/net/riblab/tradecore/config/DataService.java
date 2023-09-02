package net.riblab.tradecore.config;

import javax.annotation.Nullable;
import java.io.File;

public interface DataService {

    static DataService getImpl(File dataFolder) {
        return new DataServiceImpl(dataFolder);
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
