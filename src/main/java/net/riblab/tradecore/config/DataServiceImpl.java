package net.riblab.tradecore.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.Getter;
import net.riblab.tradecore.TradeCore;
import net.riblab.tradecore.craft.CraftingRecipesRegistry;
import net.riblab.tradecore.craft.ITCCraftingRecipe;
import net.riblab.tradecore.craft.TCCraftingRecipe;
import net.riblab.tradecore.craft.TCCraftingRecipes;
import net.riblab.tradecore.item.base.ITCItem;
import net.riblab.tradecore.item.base.TCItemRegistry;
import org.bukkit.Bukkit;
import org.codehaus.plexus.util.FileUtils;
import org.codehaus.plexus.util.StringUtils;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.introspector.BeanAccess;
import org.yaml.snakeyaml.nodes.*;
import org.yaml.snakeyaml.representer.Representer;

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
    
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    private static final Yaml yaml;

    static{
        DumperOptions options = new DumperOptions();
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK); // フロースタイルを指定
        options.setAllowReadOnlyProperties(true);
        Representer representer = new Representer(options);
        representer.addClassTag(TCCraftingRecipe.class, Tag.MAP);
        representer.getPropertyUtils().setBeanAccess(BeanAccess.FIELD);
        
        yaml = new Yaml(representer);
    }

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
        saveWithJson(currencyData, currencyDataFile);
        saveWithJson(jobDatas, jobsDataFile);
    }
    
    private void saveWithJson(Object dataInstance, File file){
        String str = gson.toJson(dataInstance);
        try {
            FileUtils.forceMkdir(new File(file.getParent()));
            FileUtils.fileWrite(file, str);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void loadAll() {
        currencyData =  load(currencyDataFile, CurrencyData.class);
        jobDatas = load(jobsDataFile, JobDatas.class);
        loadItems();
        loadCraftingRecipes();
    }

    /**
     * あるデータファイル(.yml)からあるデータタイプを読み込んで返す
     */
    private <T> T load(File dataFile, Class<T> dataType){
        String str = null;
        try {
            str =  FileUtils.fileRead(dataFile);
        } catch (IOException ignored) {
        }
        
        if(!StringUtils.isEmpty(str))
            return gson.fromJson(str, dataType);
        else 
            return null;
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
            List<ITCItem> deserializedItems = ItemIOUtils.deserialize(itemFile);
            TCItemRegistry.INSTANCE.getDeserializedItems().addAll(deserializedItems);
        }
    }
    
    public void exportItem(ITCItem item){
        ItemIOUtils.saveItem(List.of(item), itemExportFile);
    }

    public void exportItem(List<ITCItem> items){
        ItemIOUtils.saveItem(items, itemExportFile);
    }
    
    //TODO:引数をObjectにしてexportAsYamlみたいな感じにする
    public void exportCraftingRecipes(List<TCCraftingRecipe> craftingRecipes){
        Map<String, TCCraftingRecipe> craftingRecipesMap = new HashMap<>();
        craftingRecipes.forEach(tcCraftingRecipe -> craftingRecipesMap.put(tcCraftingRecipe.getResult(), tcCraftingRecipe));
        if(!craftingRecipeExportFile.getParentFile().exists())
            craftingRecipeExportFile.getParentFile().mkdirs();
        FileUtils.getFile(craftingRecipeExportFile.toString());
        try (FileWriter writer = new FileWriter(craftingRecipeExportFile)) {
            yaml.dump(craftingRecipesMap, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
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
            composeCraftingRecipes(craftingRecipeFile);
        }
    }
    
    private void composeCraftingRecipes(File craftingRecipeFile){
        
        List<ITCCraftingRecipe> deserializedRecipes = new ArrayList<>();
        try (FileReader reader = new FileReader(craftingRecipeFile)) {
            // YAMLデータを読み込み、ルートノードを取得
            Node rootNode = yaml.compose(reader);

            if (rootNode instanceof MappingNode mappingNode) {
                // マップ内のアイテムを1個ずつ取得
                Iterator<NodeTuple> iterator2 = mappingNode.getValue().iterator();
                while (iterator2.hasNext()) {
                    NodeTuple nodeTuple2 = iterator2.next();
                    ScalarNode internalNameNode = (ScalarNode) nodeTuple2.getKeyNode();

                    TCCraftingRecipe tcCraftingRecipe = new TCCraftingRecipe();
//                    tcCraftingRecipe.setInternalName(internalNameNode.getValue());

                    Node valueNode2 = nodeTuple2.getValueNode();
                    if(valueNode2 instanceof MappingNode valueNode2Map){
                        Iterator<NodeTuple> iterator3 = valueNode2Map.getValue().iterator();
                        while (iterator3.hasNext()) {
                            NodeTuple nodeTuple3 = iterator3.next();

                            ScalarNode itemPropertiesNode = (ScalarNode) nodeTuple3.getKeyNode();//category, fee...
                            
                            if(itemPropertiesNode.getValue().equals("category")){
                                TCCraftingRecipes.RecipeType category = TCCraftingRecipes.RecipeType.valueOf(((ScalarNode) nodeTuple3.getValueNode()).getValue());
                                tcCraftingRecipe.setCategory(category);
                            }
                            else if(itemPropertiesNode.getValue().equals("fee")){
                                double fee = Double.parseDouble(((ScalarNode) nodeTuple3.getValueNode()).getValue());
                                tcCraftingRecipe.setFee(fee);
                            }
                            else if(itemPropertiesNode.getValue().equals("ingredients")){
                                Node valueNode3 = nodeTuple3.getValueNode();
                                Map<String, Integer> ingredientsMap = new HashMap<>();
                                if(valueNode3 instanceof MappingNode valueNode3Map){
                                    Iterator<NodeTuple> iterator4 = valueNode3Map.getValue().iterator();
                                    while (iterator4.hasNext()) {
                                        NodeTuple nodeTuple4 = iterator4.next();
                                        String ingredientName = ((ScalarNode) nodeTuple4.getKeyNode()).getValue();
                                        int ingredientAmount = Integer.parseInt(((ScalarNode) nodeTuple4.getValueNode()).getValue());
                                        ingredientsMap.put(ingredientName, ingredientAmount);
                                    }
                                }
                                tcCraftingRecipe.setIngredients(ingredientsMap);
                            }
                            else if(itemPropertiesNode.getValue().equals("result")){
                                tcCraftingRecipe.setResult(((ScalarNode) nodeTuple3.getValueNode()).getValue());
                            }
                            else if(itemPropertiesNode.getValue().equals("resultAmount")){
                                int resultAmount = Integer.parseInt(((ScalarNode) nodeTuple3.getValueNode()).getValue());
                                tcCraftingRecipe.setResultAmount(resultAmount);
                            }
                        }
                    }
                    deserializedRecipes.add(tcCraftingRecipe);
                }
            }
        } catch (IOException e) {
            Bukkit.getLogger().severe("ファイルの解析に失敗しました: " + craftingRecipeFile);
            e.printStackTrace();
        }
        CraftingRecipesRegistry.INSTANCE.getDeserializedCraftingRecipes().addAll(deserializedRecipes);
    }
}