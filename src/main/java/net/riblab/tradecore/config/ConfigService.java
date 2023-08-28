package net.riblab.tradecore.config;

import javax.annotation.Nullable;

public interface ConfigService {
    
    static ConfigService getImpl(){
        return new ConfigServiceImpl();
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
