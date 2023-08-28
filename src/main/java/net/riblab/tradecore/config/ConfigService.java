package net.riblab.tradecore.config;

import net.riblab.tradecore.config.ConfigServiceImpl.CurrencyData;
import net.riblab.tradecore.config.ConfigServiceImpl.JobDatas;

import javax.annotation.Nullable;

public interface ConfigService {
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
