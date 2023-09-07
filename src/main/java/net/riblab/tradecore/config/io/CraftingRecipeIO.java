/*
 * Copyright (c) 2023. RIBLaB 
 */
package net.riblab.tradecore.config.io;

import net.riblab.tradecore.craft.CraftingRecipesRegistry;
import net.riblab.tradecore.craft.ITCCraftingRecipe;
import net.riblab.tradecore.craft.TCCraftingRecipe;
import net.riblab.tradecore.general.ErrorMessages;
import org.bukkit.Bukkit;
import org.codehaus.plexus.util.FileUtils;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.introspector.BeanAccess;
import org.yaml.snakeyaml.nodes.*;
import org.yaml.snakeyaml.representer.Representer;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

import static net.riblab.tradecore.config.io.CraftingRecipeIOTags.*;

public class CraftingRecipeIO {

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

    public static void deserialize(File craftingRecipeFile){

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
                    tcCraftingRecipe.setInternalName(internalNameNode.getValue());

                    Node valueNode2 = nodeTuple2.getValueNode();
                    if(valueNode2 instanceof MappingNode valueNode2Map){
                        Iterator<NodeTuple> iterator3 = valueNode2Map.getValue().iterator();
                        while (iterator3.hasNext()) {
                            NodeTuple nodeTuple3 = iterator3.next();

                            ScalarNode itemPropertiesNode = (ScalarNode) nodeTuple3.getKeyNode();//category, fee...

                            if(itemPropertiesNode.getValue().equals(CATEGORY.get())){
                                parseCategory(tcCraftingRecipe, nodeTuple3);
                            }
                            else if(itemPropertiesNode.getValue().equals(FEE.get())){
                                parseFee(tcCraftingRecipe, nodeTuple3);
                            }
                            else if(itemPropertiesNode.getValue().equals(INGREDIENTS.get())){
                                parseIngredients(tcCraftingRecipe, nodeTuple3);
                            }
                            else if(itemPropertiesNode.getValue().equals(RESULT.get())){
                                parseResult(tcCraftingRecipe, nodeTuple3);
                            }
                            else if(itemPropertiesNode.getValue().equals(RESULTAMOUNT.get())){
                                    parseResultAmount(tcCraftingRecipe, nodeTuple3);
                            }
                        }
                    }
                    deserializedRecipes.add(tcCraftingRecipe);
                }
            }
        } catch (IOException e) {
            Bukkit.getLogger().severe(ErrorMessages.FAILED_TO_PARSE_FILE.get() + craftingRecipeFile);
            e.printStackTrace();
        }
        CraftingRecipesRegistry.INSTANCE.addAll(deserializedRecipes);
    }

    private static void parseResultAmount(TCCraftingRecipe tcCraftingRecipe, NodeTuple nodeTuple3) {
        int resultAmount = Integer.parseInt(((ScalarNode) nodeTuple3.getValueNode()).getValue());
        tcCraftingRecipe.setResultAmount(resultAmount);
    }

    private static void parseResult(TCCraftingRecipe tcCraftingRecipe, NodeTuple nodeTuple3) {
        tcCraftingRecipe.setResult(((ScalarNode) nodeTuple3.getValueNode()).getValue());
    }

    private static void parseIngredients(TCCraftingRecipe tcCraftingRecipe, NodeTuple nodeTuple3) {
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

    private static void parseFee(TCCraftingRecipe tcCraftingRecipe, NodeTuple nodeTuple3) {
        double fee = Double.parseDouble(((ScalarNode) nodeTuple3.getValueNode()).getValue());
        tcCraftingRecipe.setFee(fee);
    }

    private static void parseCategory(TCCraftingRecipe tcCraftingRecipe, NodeTuple nodeTuple3) {
        CraftingRecipesRegistry.RecipeType category = CraftingRecipesRegistry.RecipeType.valueOf(((ScalarNode) nodeTuple3.getValueNode()).getValue());
        tcCraftingRecipe.setCategory(category);
    }

    public static void serialize(List<ITCCraftingRecipe> craftingRecipes, File file){
        Map<String, Object> craftingRecipesMap = new HashMap<>();
        
        for (ITCCraftingRecipe craftingRecipe : craftingRecipes) {
            Map<String, Object> craftingRecipeParams = new HashMap<>();
            craftingRecipeParams.put(INGREDIENTS.get(), craftingRecipe.getIngredients());
            craftingRecipeParams.put(RESULT.get(), craftingRecipe.getResult());
            craftingRecipeParams.put(RESULTAMOUNT.get(), craftingRecipe.getResultAmount());
            craftingRecipeParams.put(FEE.get(), craftingRecipe.getFee());
            craftingRecipeParams.put(CATEGORY.get(), craftingRecipe.getCategory().name());

            craftingRecipesMap.put(craftingRecipe.getInternalName(), craftingRecipeParams);
        }
        
        if(!file.getParentFile().exists())
            file.getParentFile().mkdirs();
        FileUtils.getFile(file.toString());
        try (FileWriter writer = new FileWriter(file)) {
            yaml.dump(craftingRecipesMap, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
