package net.riblab.tradecore.item;

import lombok.Data;
import net.riblab.tradecore.Materials;
import org.bukkit.Material;

import java.util.*;

public enum LootTables {
    GRASS(new LootTable(Set.of(Material.GRASS, Material.TALL_GRASS), TCTool.ToolType.HAND, 0, Map.of(0.2f, TCItems.PEBBLE.get()))),
    LEAVES(new LootTable(Materials.leaves, TCTool.ToolType.AXE, 0, Map.of(0.2f, TCItems.STICK.get()))),
    LOGS(new LootTable(Materials.logs, TCTool.ToolType.AXE, 1, Map.of(1f, TCItems.STICK.get()))); //TODO:新アイテム
    
    private final LootTable table;

    LootTables(LootTable table) {
        this.table = table;
    }

    public LootTable get() {
        return table;
    }

    /**
     * マテリアルとツールタイプに一致するルートテーブルを洗い出す
     * @return ルートテーブル
     */
    public static Map<Float, ITCItem> get(Material material, TCTool.ToolType toolType){
        Map<Float, ITCItem> itemMap = new HashMap<>();
        List<Map<Float, ITCItem>> itemMaps = Arrays.stream(LootTables.values()).map(LootTables::get).filter(table1 -> table1.getMaterial().contains(material)).filter(table1 -> table1.getToolType() == toolType)
                .map(LootTable::getDropChanceMap).toList();
        itemMaps.forEach(itemMap::putAll);
        return itemMap;
    }

    /**
     * マテリアルとツールタイプに一致し、採掘レベルの条件を満たすルートテーブルを洗い出す
     * @return ルートテーブル
     */
    public static Map<Float, ITCItem> get(Material material, TCTool tool){
        Map<Float, ITCItem> itemMap = new HashMap<>();
        List<Map<Float, ITCItem>> itemMaps = Arrays.stream(LootTables.values())
                .map(LootTables::get)
                .filter(table1 -> table1.getMaterial().contains(material))
                .filter(table1 -> table1.getToolType() == tool.getToolType())
                .filter(lootTable -> lootTable.getHarvestLevel() <= tool.getHarvestLevel())
                .map(LootTable::getDropChanceMap).toList();
        itemMaps.forEach(itemMap::putAll);
        return itemMap;
    }
    
    @Data
    public static class LootTable{
        /**
         * ドロップテーブルの対象アイテム
         */
        private final Set<Material> material;
        
        /**
         * ドロップテーブルを発動させるのに必要なツールの種類
         */
        private final TCTool.ToolType toolType;

        /**
         * ドロップテーブルを発動させるために必要な最小のツールレベル
         */
        private final int harvestLevel;

        /**
         * ドロップ率(0~1)とその確率でドロップするアイテムのマップ
         */
        private final Map<Float, ITCItem> dropChanceMap;
    }
}
