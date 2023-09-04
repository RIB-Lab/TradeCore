package net.riblab.tradecore.config;

import net.riblab.tradecore.item.base.ITCItem;

import javax.annotation.Nullable;

public interface DataService {

    static DataService getImpl() {
        return DataServiceImpl.INSTANCE;
    }

    /**
     * プラグインの保存できる全てのデータを保存する
     */
    void saveAll();

    /**
     * プラグインの全てのデータをロードする
     */
    void loadAll();

    /**
     * プラグインのアイテムレジストリだけをロードする
     */
    void loadItems();

    @Nullable
    CurrencyData getCurrencyData();

    @Nullable
    JobDatas getJobDatas();

    /**
     * アイテムをファイルにスクリプトとしてエクスポートする
     * @param item アイテム
     */
    void exportItem(ITCItem item);
}
