/*
 * Copyright (c) 2023. RIBLaB
 */
package net.riblab.tradecore.config;

import lombok.Getter;
import net.riblab.tradecore.config.io.*;
import net.riblab.tradecore.craft.CraftingRecipesRegistry;
import net.riblab.tradecore.craft.ITCCraftingRecipe;
import net.riblab.tradecore.general.IRegistry;
import net.riblab.tradecore.item.ILootTable;
import net.riblab.tradecore.item.LootTableRegistry;
import net.riblab.tradecore.item.MaterialSetRegistry;
import net.riblab.tradecore.item.base.ITCItem;
import net.riblab.tradecore.item.base.TCItemRegistry;
import org.bukkit.Material;
import org.codehaus.plexus.util.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
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
    
    private final InterfaceIO<Map<String, ITCItem>> itemIO = new ItemIO();
    private final InterfaceIO<Map<String, Set<Material>>> materialSetIO = new MaterialSetIO();
    private final InterfaceIO<Map<String, ITCCraftingRecipe>> craftingRecipeIO = new CraftingRecipeIO();
    private final InterfaceIO<Map<String, ILootTable>> lootTableIO = new LootTableIO();

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
        load(CraftingRecipesRegistry.INSTANCE, DataPaths.CRAFT_RECIPE_DIR.get() ,craftingRecipeIO);
        load(MaterialSetRegistry.INSTANCE, DataPaths.MATERIAL_SET_DIR.get(), materialSetIO);
        load(LootTableRegistry.INSTANCE, DataPaths.LOOT_TABLE_DIR.get(), lootTableIO);
        
        postLoad();
    }

    /**
     * 全てのロードが終わった後に挟む処理
     */
    private void postLoad(){
        CraftingRecipesRegistry.INSTANCE.validate();
    }

    public void exportItem(ITCItem item) {
        itemIO.serialize(Map.of(item.getInternalName(), item), DataPaths.ITEM_EXPORT_FILE.get());
    }

    public void exportItem(List<ITCItem> items) {
        Map<String, ITCItem> itemsMap = new HashMap<>();
        for (ITCItem item : items) {
            itemsMap.put(item.getInternalName(), item);
        }
        itemIO.serialize(itemsMap, DataPaths.ITEM_EXPORT_FILE.get());
    }

    public void exportCraftingRecipes(List<ITCCraftingRecipe> craftingRecipes) {
        Map<String, ITCCraftingRecipe> craftingRecipesMap = new HashMap<>();
        for (ITCCraftingRecipe craftingRecipe : craftingRecipes) {
            craftingRecipesMap.put(craftingRecipe.getInternalName(), craftingRecipe);
        }
        craftingRecipeIO.serialize(craftingRecipesMap, DataPaths.CRAFT_RECIPE_EXPORT_FILE.get());
    }
    
    public void loadItems(){
        load(TCItemRegistry.INSTANCE, DataPaths.ITEM_DIR.get(), itemIO);
    }

    public void exportMaterialSets(Map<String, Set<Material>> materialSets) {
        materialSetIO.serialize(materialSets, DataPaths.MATERIAL_SET_EXPORT_FILE.get());
    }
    
    public <T> void load(IRegistry<T> registry, File pathToLoad, InterfaceIO<T> interfaceIO){
        registry.clear();
        List<File> files;
        try {
            FileUtils.forceMkdir(pathToLoad);
            files = FileUtils.getFiles(pathToLoad, null, null);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        for (File file : files) {
            final T deserializedObjects = interfaceIO.deserialize(file);
            registry.addAll(deserializedObjects);
        }
    }

    public void exportLootTables(Map<String, ILootTable> lootTables) {
        lootTableIO.serialize(lootTables, DataPaths.LOOT_TABLE_EXPORT_FILE.get());
    }
}