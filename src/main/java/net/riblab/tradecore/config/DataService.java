/*
 * Copyright (c) 2023. RIBLaB 
 */
package net.riblab.tradecore.config;

import net.riblab.tradecore.craft.ITCCraftingRecipe;
import net.riblab.tradecore.craft.TCCraftingRecipe;
import net.riblab.tradecore.item.base.ITCItem;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public sealed interface DataService permits DataServiceImpl {

    static DataService getImpl() {
        return DataServiceImpl.INSTANCE;
    }

    /**
     * プラグインの保存できる全てのデータを保存する(クラフトレシピなどは自動)
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

    /**
     * アイテム達をファイルにスクリプトとしてエクスポートする
     */
    void exportItem(List<ITCItem> items);

    /**
     * クラフトレシピ達を既定のファイルにエクスポートする
     */
    void exportCraftingRecipes(List<ITCCraftingRecipe> craftingRecipes);

    /**
     * クラフトレシピをデータフォルダから読み込む
     */
    void loadCraftingRecipes();
}
