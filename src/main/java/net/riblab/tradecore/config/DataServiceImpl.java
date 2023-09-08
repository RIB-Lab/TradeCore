/*
 * Copyright (c) 2023. RIBLaB
 */
package net.riblab.tradecore.config;

import lombok.Getter;
import net.riblab.tradecore.config.io.CraftingRecipeIO;
import net.riblab.tradecore.config.io.ItemIO;
import net.riblab.tradecore.config.io.JsonIO;
import net.riblab.tradecore.config.io.MaterialSetIO;
import net.riblab.tradecore.craft.CraftingRecipesRegistry;
import net.riblab.tradecore.craft.ITCCraftingRecipe;
import net.riblab.tradecore.item.MaterialSetRegistry;
import net.riblab.tradecore.item.base.ITCItem;
import net.riblab.tradecore.item.base.TCItemRegistry;
import org.bukkit.Material;
import org.codehaus.plexus.util.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * コンフィグ管理システム
 */
enum DataServiceImpl implements DataService {
    INSTANCE;

    /**
     * 保存するコンフィグの実体
     */
    @Getter
    private CurrencyData currencyData = new CurrencyData();
    @Getter
    private JobDatas jobDatas = new JobDatas();

    @Override
    public void saveAll() {
        JsonIO.saveWithJson(currencyData, DataPaths.CURRENCY_DATA_FILE.get());
        JsonIO.saveWithJson(jobDatas, DataPaths.JOBS_DATA_FILE.get());
    }

    @Override
    public void loadAll() {
        currencyData = JsonIO.loadAsJson(DataPaths.CURRENCY_DATA_FILE.get(), CurrencyData.class);
        jobDatas = JsonIO.loadAsJson(DataPaths.JOBS_DATA_FILE.get(), JobDatas.class);
        loadItems();
        loadCraftingRecipes();
        loadMaterialSets();
        
        postLoad();
    }

    /**
     * 全てのロードが終わった後に挟む処理
     */
    private void postLoad(){
        CraftingRecipesRegistry.INSTANCE.validate();
    }

    public void loadItems() {
        TCItemRegistry.INSTANCE.clear();

        List<File> itemFiles;
        try {
            itemFiles = FileUtils.getFiles(DataPaths.ITEM_DIR.get(), null, null);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        for (File itemFile : itemFiles) {
            Map<String, ITCItem> deserializedItems = ItemIO.deserialize(itemFile);
            TCItemRegistry.INSTANCE.addAll(deserializedItems);
        }
    }

    public void exportItem(ITCItem item) {
        ItemIO.serializeItem(List.of(item), DataPaths.ITEM_EXPORT_FILE.get());
    }

    public void exportItem(List<ITCItem> items) {
        ItemIO.serializeItem(items, DataPaths.ITEM_EXPORT_FILE.get());
    }

    public void loadCraftingRecipes() {
        CraftingRecipesRegistry.INSTANCE.clear();
        List<File> craftingRecipeFiles;
        try {
            craftingRecipeFiles = FileUtils.getFiles(DataPaths.CRAFT_RECIPE_DIR.get(), null, null);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        for (File craftingRecipeFile : craftingRecipeFiles) {
            final List<ITCCraftingRecipe> deserializedRecipes = CraftingRecipeIO.deserialize(craftingRecipeFile);
            CraftingRecipesRegistry.INSTANCE.addAll(deserializedRecipes);
        }
    }

    public void exportCraftingRecipes(List<ITCCraftingRecipe> craftingRecipes) {
        CraftingRecipeIO.serialize(craftingRecipes, DataPaths.CRAFT_RECIPE_EXPORT_FILE.get());
    }
    
    //TODO: 3クラス分溜まったので共通化
    public void loadMaterialSets(){
        MaterialSetRegistry.INSTANCE.clear();
        List<File> materialSetFiles;
        try {
            materialSetFiles = FileUtils.getFiles(DataPaths.MATERIAL_SET_DIR.get(), null, null);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        for (File materialSetFile : materialSetFiles) {
            final Map<String, Set<Material>> deserializedmaterials = MaterialSetIO.deserialize(materialSetFile);
            MaterialSetRegistry.INSTANCE.putAll(deserializedmaterials);
        }
    }

    public void exportMaterialSets(Map<String, Set<Material>> materialSets) {
        MaterialSetIO.serialize(materialSets, DataPaths.MATERIAL_SET_EXPORT_FILE.get());
    }
}