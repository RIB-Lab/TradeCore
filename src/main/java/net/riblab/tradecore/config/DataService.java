package net.riblab.tradecore.config;

import net.riblab.tradecore.item.base.ITCItem;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public sealed interface DataService permits DataServiceImpl {

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


    @Nonnull
    CurrencyData getCurrencyData();

    @Nonnull
    JobDatas getJobDatas();

    /**
     * アイテムをファイルにスクリプトとしてエクスポートする
     * @param item アイテム
     */
    void exportItem(ITCItem item);
}
