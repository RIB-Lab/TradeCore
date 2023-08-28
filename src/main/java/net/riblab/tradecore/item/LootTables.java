package net.riblab.tradecore.item;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import lombok.Data;
import net.riblab.tradecore.general.utils.Materials;
import net.riblab.tradecore.item.base.ITCItem;
import net.riblab.tradecore.item.base.ITCTool;
import net.riblab.tradecore.item.base.TCTool;
import org.bukkit.Material;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.*;

/**
 * ブロックを壊したとき落ちるアイテムを記述したルートテーブルの一覧
 */
public enum LootTables {
    GRASS(new LootTable(Set.of(Material.GRASS, Material.TALL_GRASS), TCTool.ToolType.HAND, 0, Map.of(1f, TCItems.DRYGRASS.get(), 0.2f, TCItems.PEBBLE.get()))),
    LEAVES(new LootTable(Materials.leaves, TCTool.ToolType.AXE, 0, Map.of(0.2f, TCItems.STICK.get()))),
    LEAVES2(new LootTable(Materials.leaves, TCTool.ToolType.AXE, 1, Map.of(0.1f, TCItems.STICK.get()))),
    LOGS(new LootTable(Materials.logs, TCTool.ToolType.AXE, 1, Map.of(0.2f, TCItems.ROUND_TRUNK.get(), 0.4f, TCItems.BARK.get(), 0.8f, TCItems.TWIG.get()))),
    LOGS2(new LootTable(Materials.logs, TCTool.ToolType.AXE, 2, Map.of(0.2f, TCItems.ROUND_TRUNK.get()))),
    LOGS3(new LootTable(Materials.logs, TCTool.ToolType.AXE, 3, Map.of(0.1f, TCItems.ROUND_TRUNK.get()))),
    DIRTS(new LootTable(Materials.dirts, TCTool.ToolType.SHOVEL, 0, Map.of(0.2f, TCItems.DUST.get(), 0.1f, TCItems.MUD.get()))),
    DIRTS2(new LootTable(Materials.dirts, TCTool.ToolType.SHOVEL, 1, Map.of(0.1f, TCItems.DUST.get(), 0.05f, TCItems.MUD.get()))),
    PRIMITIVESTONES(new LootTable(Materials.primitiveStones, TCTool.ToolType.PICKAXE, 0, Map.of(0.2f, TCItems.WIDESTONE.get(), 0.21f, TCItems.TALLSTONE.get()))),
    PRIMITIVESTONES2(new LootTable(Materials.primitiveStones, TCTool.ToolType.PICKAXE, 1, Map.of(0.1f, TCItems.ROUND_STONE.get()))),
    PRIMITIVEPLANKS(new LootTable(Materials.primitivePlanks, TCTool.ToolType.AXE, 2, Map.of(1.0f, TCItems.WOODPULP.get()))),
    MOSS(new LootTable(Set.of(Material.FARMLAND), TCTool.ToolType.HOE, 0, Map.of(0.2f, TCItems.MOSS.get(), 1.0f, TCItems.DRYGRASS.get()))),
    MOSS2(new LootTable(Set.of(Material.FARMLAND), TCTool.ToolType.HOE, 0, Map.of(0.1f, TCItems.MOSS.get(), 0.5f, TCItems.DRYGRASS.get()))),
    ANDESITE(new LootTable(Set.of(Material.ANDESITE), TCTool.ToolType.PICKAXE, 1, Map.of(0.2f, TCItems.ANDESITE_STONE.get()))),
    GRANITE(new LootTable(Set.of(Material.GRANITE), TCTool.ToolType.PICKAXE, 1, Map.of(0.2f, TCItems.GRANITE_STONE.get()))),
    DIORITE(new LootTable(Set.of(Material.DIORITE), TCTool.ToolType.PICKAXE, 1, Map.of(0.2f, TCItems.DIORITE_STONE.get()))),
    GRAVEL(new LootTable(Set.of(Material.GRAVEL), TCTool.ToolType.SHOVEL, 1, Map.of(0.2f, TCItems.GRAVEL_DUST.get(), 0.02f, TCItems.FLINT.get(), 0.021f, TCItems.METEORIC_IRON_ORE.get()))),
    SAND(new LootTable(Set.of(Material.SAND), TCTool.ToolType.SHOVEL, 1, Map.of(0.2f, TCItems.SAND_DUST.get(), 0.02f, TCItems.SANDGOLD.get())));

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
    @ParametersAreNonnullByDefault
    public static Multimap<Float, ITCItem> get(Material material, TCTool.ToolType toolType) {
        Multimap<Float, ITCItem> itemMap = ArrayListMultimap.create();
        List<Map<Float, ITCItem>> itemMaps = Arrays.stream(LootTables.values()).map(LootTables::get).filter(table1 -> table1.getMaterial().contains(material)).filter(table1 -> table1.getToolType() == toolType)
                .map(LootTable::getDropChanceMap).toList();
        itemMaps.forEach(floatITCItemMap -> floatITCItemMap.forEach(itemMap::put));
        return itemMap;
    }

    /**
     * マテリアルとツールタイプに一致し、採掘レベルの条件を満たすルートテーブルを洗い出す
     *
     * @return ルートテーブル
     */
    @ParametersAreNonnullByDefault
    public static Multimap<Float, ITCItem> get(Material material, ITCTool tool) {
        Multimap<Float, ITCItem> itemMultiMap = ArrayListMultimap.create();
        List<Map<Float, ITCItem>> itemMaps = Arrays.stream(LootTables.values())
                .map(LootTables::get)
                .filter(table1 -> table1.getMaterial().contains(material))
                .filter(table1 -> table1.getToolType() == tool.getToolType())
                .filter(lootTable -> lootTable.getHarvestLevel() <= tool.getHarvestLevel())
                .map(LootTable::getDropChanceMap).toList();
        itemMaps.forEach(floatITCItemMap -> floatITCItemMap.forEach(itemMultiMap::put));
        return itemMultiMap;
    }

    /**
     * あるマテリアルをあるツールで掘るために必要な最小硬度を取得
     */
    @ParametersAreNonnullByDefault
    public static int getMinHardness(Material material, ITCTool tool){
        List<Integer> hardnessList = Arrays.stream(LootTables.values())
                .map(LootTables::get)
                .filter(table1 -> table1.getMaterial().contains(material))
                .filter(table1 -> table1.getToolType() == tool.getToolType())
                .map(LootTable::getHarvestLevel).toList();
        if(hardnessList.size() == 0)
            return Integer.MAX_VALUE;
        
        return Collections.min(hardnessList);
    }

    /**
     * バニラのルートテーブルとは別の概念
     */
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
