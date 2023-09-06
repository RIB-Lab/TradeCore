package net.riblab.tradecore.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.Getter;
import net.riblab.tradecore.TradeCore;
import net.riblab.tradecore.craft.CraftingRecipesRegistry;
import net.riblab.tradecore.craft.ITCCraftingRecipe;
import net.riblab.tradecore.craft.TCCraftingRecipe;
import net.riblab.tradecore.item.base.ITCItem;
import net.riblab.tradecore.item.base.TCItemRegistry;
import org.codehaus.plexus.util.FileUtils;
import org.codehaus.plexus.util.StringUtils;

import java.io.*;
import java.util.*;

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

    /**
     * コンフィグの保存Path
     */
    private final File currencyDataFile;

    /**
     * コンフィグの保存Path
     */
    private final File jobsDataFile;

    /**
     * セーブ全般の保存フォルダ
     */
    private final File saveDir;

    /**
     * アイテムデータの保存フォルダ
     */
    private final File itemDir;

    /**
     * アイテムデータのエクスポート先
     */
    private final File itemExportFile;

    /**
     * クラフトレシピのエクスポート先
     */
    private final File craftingRecipeExportFile;

    /**
     * クラフトレシピデータの保存フォルダ
     */
    private final File craftingRecipeDir;

    DataServiceImpl() {
        saveDir = new File(TradeCore.getInstance().getDataFolder(), "/saves");
        currencyDataFile = new File(saveDir, "/currency.json");
        jobsDataFile = new File(saveDir, "/jobs.json");
        itemDir = new File(TradeCore.getInstance().getDataFolder(), "items");
        itemExportFile = new File(TradeCore.getInstance().getDataFolder(), "/export/items.yml");
        craftingRecipeExportFile = new File(TradeCore.getInstance().getDataFolder(), "/export/recipes.yml");
        craftingRecipeDir = new File(TradeCore.getInstance().getDataFolder(), "crafting-recipes");
    }

    @Override
    public void saveAll() {
        JsonIO.saveWithJson(currencyData, currencyDataFile);
        JsonIO.saveWithJson(jobDatas, jobsDataFile);
    }

    @Override
    public void loadAll() {
        currencyData =  JsonIO.loadAsJson(currencyDataFile, CurrencyData.class);
        jobDatas = JsonIO.loadAsJson(jobsDataFile, JobDatas.class);
        loadItems();
        loadCraftingRecipes();
    }
    
    public void loadItems(){
        TCItemRegistry.INSTANCE.getDeserializedItems().clear();
        
        List<File> itemFiles;
        try {
            itemFiles = FileUtils.getFiles(itemDir,null,null);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        for (File itemFile : itemFiles) {
            List<ITCItem> deserializedItems = ItemIO.deserialize(itemFile);
            TCItemRegistry.INSTANCE.getDeserializedItems().addAll(deserializedItems);
        }
    }
    
    public void exportItem(ITCItem item){
        ItemIO.serializeItem(List.of(item), itemExportFile);
    }

    public void exportItem(List<ITCItem> items){
        ItemIO.serializeItem(items, itemExportFile);
    }
    
    public void loadCraftingRecipes(){
        CraftingRecipesRegistry.INSTANCE.getDeserializedCraftingRecipes().clear();
        List<File> craftingRecipeFiles;
        try {
            craftingRecipeFiles = FileUtils.getFiles(craftingRecipeDir,null,null);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        
        for (File craftingRecipeFile : craftingRecipeFiles) {
            CraftingRecipeIO.deserialize(craftingRecipeFile);
        }
    }

    public void exportCraftingRecipes(List<ITCCraftingRecipe> craftingRecipes){
        CraftingRecipeIO.serialize(craftingRecipes, craftingRecipeExportFile);
    }
}