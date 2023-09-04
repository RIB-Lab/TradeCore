package net.riblab.tradecore.config;

import net.riblab.tradecore.item.base.ITCItem;

import javax.annotation.Nullable;
import java.io.File;

public interface DataService {

    static DataService getImpl() {
        return DataServiceImpl.INSTANCE;
    }

    /**
     * コンフィグを保存する
     */
    void save();

    /**
     * コンフィグをロードする
     */
    void load();
    
    void loadItems();

    @Nullable
    CurrencyData getCurrencyData();

    @Nullable
    JobDatas getJobDatas();

    void exportItem(ITCItem item);
}
