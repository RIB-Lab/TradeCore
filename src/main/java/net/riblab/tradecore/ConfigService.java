package net.riblab.tradecore;

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
