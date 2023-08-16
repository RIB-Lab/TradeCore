package net.riblab.tradecore.item;

import lombok.Data;
import net.riblab.tradecore.Materials;
import org.bukkit.Material;

import java.util.*;

public enum LootTables {
    GRASS(new LootTable(Set.of(Material.GRASS, Material.TALL_GRASS), TCTool.ToolType.HAND, 0, Map.of(1f, TCItems.DRYGRASS.get(), 0.2f, TCItems.PEBBLE.get()))),
    LEAVES(new LootTable(Materials.leaves, TCTool.ToolType.AXE, 0, Map.of(0.2f, TCItems.STICK.get()))),
    LOGS(new LootTable(Materials.logs, TCTool.ToolType.AXE, 1, Map.of(0.2f, TCItems.ROUND_TRUNK.get(), 0.4f, TCItems.BARK.get(), 0.8f, TCItems.TWIG.get()))),
    DIRTS(new LootTable(Materials.dirts, TCTool.ToolType.SHOVEL, 0, Map.of(0.2f, TCItems.DUST.get(), 0.1f, TCItems.MUD.get()))),
    PRIMITIVESTONES(new LootTable(Materials.primitiveStones, TCTool.ToolType.PICKAXE, 0, Map.of(0.2f, TCItems.WIDESTONE.get(), 0.21f, TCItems.TALLSTONE.get()))),
    PRIMITIVEPLANKS(new LootTable(Materials.primitivePlanks, TCTool.ToolType.AXE, 2, Map.of(1.0f, TCItems.WOODPULP.get()))),
    MOSS(new LootTable(Set.of(Material.FARMLAND), TCTool.ToolType.HOE, 0, Map.of(0.2f, TCItems.MOSS.get(), 1.0f, TCItems.DRYGRASS.get()))),
    LOGS2(new LootTable(Materials.logs, TCTool.ToolType.AXE, 2, Map.of(0.2f, TCItems.ROUND_TRUNK.get())));//木の斧の採掘量バフ


    private final LootTable table;

    LootTables(LootTable table) {
        this.table = table;
    }

    public LootTable get() {
        return table;
    }

    /**
     * マテリアルとツールタイプに一致するルートテーブルを洗い出す
     *
     * @return ルートテーブル
     */
    public static Map<Float, ITCItem> get(Material material, TCTool.ToolType toolType) {
        Map<Float, ITCItem> itemMap = new HashMap<>();
        List<Map<Float, ITCItem>> itemMaps = Arrays.stream(LootTables.values()).map(LootTables::get).filter(table1 -> table1.getMaterial().contains(material)).filter(table1 -> table1.getToolType() == toolType)
                .map(LootTable::getDropChanceMap).toList();
        itemMaps.forEach(itemMap::putAll);
        return itemMap;
    }

    /**
     * マテリアルとツールタイプに一致し、採掘レベルの条件を満たすルートテーブルを洗い出す
     *
     * @return ルートテーブル
     */
    public static Map<Float, ITCItem> get(Material material, TCTool tool) {
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
    public static class LootTable {
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
