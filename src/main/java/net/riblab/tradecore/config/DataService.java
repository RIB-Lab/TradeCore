/*
 * Copyright (c) 2023. RIBLaB
 */
package net.riblab.tradecore.config;

import net.riblab.tradecore.craft.ITCCraftingRecipe;
import net.riblab.tradecore.loottable.ILootTable;
import net.riblab.tradecore.item.base.ITCItem;
import org.bukkit.Material;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Map;
import java.util.Set;

public sealed interface DataService permits DataServiceImpl {

    static DataService getImpl() {
        return DataServiceImpl.INSTANCE;
    }

    /**
     * プラグインの保存できる全てのデータを保存する(クラフトレシピなどは自動)
     */
    void saveAll();

    /**
     * プラグインの全てのデータをまとめてロードする
     */
    void loadAll();


    @Nonnull
    CurrencyData getCurrencyData();

    @Nonnull
    JobDatas getJobDatas();

    /**
     * アイテムをファイルにスクリプトとしてエクスポートする
     *
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

    void exportMaterialSets(Map<String, Set<Material>> materialSets);

    void exportLootTables(Map<String, ILootTable> lootTables);

    /**
     * アイテムレジストリを単品でリロードする
     */
    void loadItems();

    /**
     * マテリアルセットを単品でリロードする
     */
    void loadMaterialSet();
}
