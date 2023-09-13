package net.riblab.tradecore.config.io;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.riblab.tradecore.general.ChanceFloat;
import net.riblab.tradecore.general.ErrorMessages;
import net.riblab.tradecore.item.mod.IItemMod;
import net.riblab.tradecore.item.mod.ShortHandItemModNames;
import net.riblab.tradecore.loottable.ILootTable;
import net.riblab.tradecore.loottable.ILootTableMod;
import net.riblab.tradecore.loottable.LootTable;
import net.riblab.tradecore.loottable.ShortHandLootTableModNames;
import net.riblab.tradecore.modifier.IToolStatsModifier;
import org.bukkit.Bukkit;
import org.codehaus.plexus.util.FileUtils;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.nodes.*;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

import static net.riblab.tradecore.config.io.LootTableIOTags.*;

public final class LootTableIO implements InterfaceIO<Map<String, ILootTable>> {

    private final Gson gson = new GsonBuilder().serializeNulls().disableHtmlEscaping().create();
    private final Yaml yaml;

    public LootTableIO() {
        DumperOptions options = new DumperOptions();
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK); // フロースタイルを指定
        yaml = new Yaml(options);
    }
    
    @Override
    public Map<String, ILootTable> deserialize(File fileToLoad) {

        Map<String, ILootTable> deserializedLootTable = new HashMap<>();
        try (FileReader reader = new FileReader(fileToLoad)) {
            // YAMLデータを読み込み、ルートノードを取得
            Node rootNode = yaml.compose(reader);

            if (rootNode instanceof MappingNode mappingNode) {
                // マップ内のアイテムを1個ずつ取得
                Iterator<NodeTuple> iterator2 = mappingNode.getValue().iterator();
                while (iterator2.hasNext()) {
                    NodeTuple nodeTuple2 = iterator2.next();
                    ScalarNode lootTableNameNode = (ScalarNode) nodeTuple2.getKeyNode();

                    LootTable deserializedTable = new LootTable();
                    deserializedTable.setInternalName(lootTableNameNode.getValue());
                    
                    Node valueNode2 = nodeTuple2.getValueNode();
                    if (valueNode2 instanceof MappingNode valueNode2Map) {
                        Iterator<NodeTuple> iterator3 = valueNode2Map.getValue().iterator();
                        while (iterator3.hasNext()) {
                            NodeTuple nodeTuple3 = iterator3.next();
                            String lootProperties = ((ScalarNode) nodeTuple3.getKeyNode()).getValue();//materialset, harvestlevel...
                            if(lootProperties.equals(MATERIAL_SET.get())){
                                parseMaterialSet(deserializedTable, nodeTuple3);
                            }
                            else if(lootProperties.equals(TOOL_TYPE.get())){
                                parseToolType(deserializedTable, nodeTuple3);
                            }
                            else if(lootProperties.equals(HARVEST_LEVEL.get())){
                                parseHarvestLevel(deserializedTable, nodeTuple3);
                            }
                            else if(lootProperties.equals(DROPCHANCE_LEVEL.get())){
                                parseDropChance(deserializedTable, nodeTuple3);
                            }
                        }
                    }
                    deserializedLootTable.put(lootTableNameNode.getValue(), deserializedTable);
                }
            }
        } catch (IOException e) {
            Bukkit.getLogger().severe(ErrorMessages.FAILED_TO_PARSE_FILE.get() + fileToLoad);
            e.printStackTrace();
        }

        return deserializedLootTable;
    }

    private static void parseDropChance(LootTable deserializedTable, NodeTuple nodeTuple3) {
        if(nodeTuple3.getValueNode() instanceof MappingNode valueNode3Map){
            Map<String, ChanceFloat> chanceMap = new HashMap<>();
            Iterator<NodeTuple> iterator4 = valueNode3Map.getValue().iterator();
            while (iterator4.hasNext()) {
                NodeTuple nodeTuple4 = iterator4.next(); //sand_dust: 0.2c
                String itemName = ((ScalarNode)nodeTuple4.getKeyNode()).getValue();
                ChanceFloat chance = new ChanceFloat(Float.parseFloat(((ScalarNode)nodeTuple4.getValueNode()).getValue()));
                chanceMap.put(itemName, chance);
            }
            deserializedTable.setDropChanceMap(chanceMap);
        }
    }

    private static void parseHarvestLevel(LootTable deserializedTable, NodeTuple nodeTuple3) {
        final int harvestLevel = Integer.parseInt(((ScalarNode) nodeTuple3.getValueNode()).getValue());
        deserializedTable.setHarvestLevel(harvestLevel);
    }

    private static void parseToolType(LootTable deserializedTable, NodeTuple nodeTuple3) {
        final IToolStatsModifier.ToolType toolType = IToolStatsModifier.ToolType.valueOf(((ScalarNode) nodeTuple3.getValueNode()).getValue());
        deserializedTable.setToolType(toolType);
    }

    private static void parseMaterialSet(LootTable deserializedTable, NodeTuple nodeTuple3) {
        deserializedTable.setMaterialSetKey(((ScalarNode) nodeTuple3.getValueNode()).getValue());
    }

    @Override
    public void serialize(Map<String, ILootTable> objectToSave, File fileToSave) {
        Map<String, Object> lootTableReady = new HashMap<>();
        
        for (Map.Entry<String, ILootTable> entry : objectToSave.entrySet()) {
            Map<String, Object> modsMap = new HashMap<>();
            for (ILootTableMod<?> mod : entry.getValue().getMods()) {
                String key = ShortHandLootTableModNames.getShortHandNameFromClass((Class<? extends ILootTableMod<?>>) mod.getClass());
                String value = gson.toJson(mod.getParam());
                modsMap.put(key, value);
            }
            
            Map<String, Object> lootTableParams = new HashMap<>();
            lootTableParams.put(MODS.get(), modsMap);
            lootTableParams.put(MATERIAL_SET.get(), entry.getValue().getMaterialSetKey());
            lootTableParams.put(TOOL_TYPE.get(), entry.getValue().getToolType().toString());
            lootTableParams.put(HARVEST_LEVEL.get(), entry.getValue().getHarvestLevel());
            
            Map<String, Float> dropChanceMapFloat = new HashMap<>();
            entry.getValue().getDropChanceMap().forEach((s, chanceFloat) -> dropChanceMapFloat.put(s, chanceFloat.get()));
            lootTableParams.put(DROPCHANCE_LEVEL.get(), dropChanceMapFloat);

            lootTableReady.put(entry.getKey(), lootTableParams);
        }

        if (!fileToSave.getParentFile().exists())
            fileToSave.getParentFile().mkdirs();
        FileUtils.getFile(fileToSave.toString());
        try (FileWriter writer = new FileWriter(fileToSave)) {
            yaml.dump(lootTableReady, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
