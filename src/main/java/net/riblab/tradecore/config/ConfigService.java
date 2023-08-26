package net.riblab.tradecore.config;

public interface ConfigService {
    /**
     * コンフィグを保存する
     */
    void save();

    /**
     * コンフィグをロードする
     */
    void load();

    ConfigServiceImpl.CurrencyData getCurrencyData();

    ConfigServiceImpl.JobDatas getJobDatas();
}
