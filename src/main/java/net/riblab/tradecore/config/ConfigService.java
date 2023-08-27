package net.riblab.tradecore.config;

import net.riblab.tradecore.config.ConfigServiceImpl.CurrencyData;
import net.riblab.tradecore.config.ConfigServiceImpl.JobDatas;

public interface ConfigService {
    /**
     * コンフィグを保存する
     */
    void save();

    /**
     * コンフィグをロードする
     */
    void load();
    
    CurrencyData getCurrencyData();
    
    JobDatas getJobDatas();
}
