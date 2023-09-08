/*
 * Copyright (c) 2023. RIBLaB
 */
package net.riblab.tradecore.item;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import net.riblab.tradecore.modifier.IToolStatsModifier;
import org.bukkit.Material;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.*;

/**
 * ブロックを壊したとき落ちるアイテムを記述したルートテーブルの一覧
 */
public enum LootTables {
    GRASS(new LootTable(Set.of(Material.GRASS, Material.TALL_GRASS), IToolStatsModifier.ToolType.HAND, 0, Map.of(1f, "dryglass", 0.2f, "pebble"))),
    LEAVES(new LootTable(Materials.LEAVES.get(), IToolStatsModifier.ToolType.AXE, 0, Map.of(0.2f, "stick"))),
    LEAVES2(new LootTable(Materials.LEAVES.get(), IToolStatsModifier.ToolType.AXE, 1, Map.of(0.1f, "stick"))),
    LOGS(new LootTable(Materials.LOGS.get(), IToolStatsModifier.ToolType.AXE, 1, Map.of(0.2f, "round_trunk", 0.4f, "bark", 0.8f, "twig"))),
    LOGS2(new LootTable(Materials.LOGS.get(), IToolStatsModifier.ToolType.AXE, 2, Map.of(0.2f, "round_trunk"))),
    LOGS3(new LootTable(Materials.LOGS.get(), IToolStatsModifier.ToolType.AXE, 3, Map.of(0.1f, "round_trunk"))),
    DIRTS(new LootTable(Materials.DIRTS.get(), IToolStatsModifier.ToolType.SHOVEL, 0, Map.of(0.2f, "dust", 0.1f, "mud"))),
    DIRTS2(new LootTable(Materials.DIRTS.get(), IToolStatsModifier.ToolType.SHOVEL, 1, Map.of(0.1f, "dust", 0.05f, "mud"))),
    PRIMITIVE_STONES(new LootTable(Materials.PRIMITIVE_STONES.get(), IToolStatsModifier.ToolType.PICKAXE, 0, Map.of(0.2f, "widestone", 0.21f, "tallstone"))),
    PRIMITIVE_STONES2(new LootTable(Materials.PRIMITIVE_STONES.get(), IToolStatsModifier.ToolType.PICKAXE, 1, Map.of(0.1f, "round_stone"))),
    PRIMITIVE_PLANKS(new LootTable(Materials.PRIMITIVE_PLANKS.get(), IToolStatsModifier.ToolType.AXE, 2, Map.of(1.0f, "woodpulp"))),
    MOSS(new LootTable(Set.of(Material.FARMLAND), IToolStatsModifier.ToolType.HOE, 0, Map.of(0.2f, "moss", 1.0f, "drygrass"))),
    MOSS2(new LootTable(Set.of(Material.FARMLAND), IToolStatsModifier.ToolType.HOE, 0, Map.of(0.1f, "moss", 0.5f, "drygrass"))),
    ANDESITE(new LootTable(Set.of(Material.ANDESITE), IToolStatsModifier.ToolType.PICKAXE, 1, Map.of(0.2f, "andesite_stone"))),
    GRANITE(new LootTable(Set.of(Material.GRANITE), IToolStatsModifier.ToolType.PICKAXE, 1, Map.of(0.2f, "granite_stone"))),
    DIORITE(new LootTable(Set.of(Material.DIORITE), IToolStatsModifier.ToolType.PICKAXE, 1, Map.of(0.2f, "diorite_stone"))),
    GRAVEL(new LootTable(Set.of(Material.GRAVEL), IToolStatsModifier.ToolType.SHOVEL, 1, Map.of(0.2f, "gravel_dust", 0.02f, "flint", 0.021f, "meteoric_iron_ore"))),
    SAND(new LootTable(Set.of(Material.SAND), IToolStatsModifier.ToolType.SHOVEL, 1, Map.of(0.2f, "sand_dust", 0.02f, "sandgold")));

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
    public static Multimap<Float, String> get(Material material, IToolStatsModifier.ToolType toolType) {
        Multimap<Float, String> itemMap = ArrayListMultimap.create();
        List<Map<Float, String>> itemMaps = Arrays.stream(LootTables.values())
                .map(LootTables::get)
                .filter(table1 -> table1.material().contains(material))
                .filter(table1 -> table1.toolType() == toolType)
                .map(LootTable::dropChanceMap).toList();
        itemMaps.forEach(floatITCItemMap -> floatITCItemMap.forEach(itemMap::put));
        return itemMap;
    }

    /**
     * マテリアルとツールタイプに一致し、採掘レベルの条件を満たすルートテーブルを洗い出す
     *
     * @return ルートテーブル
     */
    @ParametersAreNonnullByDefault
    public static Multimap<Float, String> get(Material material, IToolStatsModifier toolStatsMod) {
        IToolStatsModifier.ToolStats toolStats = toolStatsMod.apply(null, null);
        Multimap<Float, String> itemMultiMap = ArrayListMultimap.create();
        List<Map<Float, String>> itemMaps = Arrays.stream(LootTables.values())
                .map(LootTables::get)
                .filter(table1 -> table1.material().contains(material))
                .filter(table1 -> table1.toolType() == toolStats.getToolType())
                .filter(lootTable -> lootTable.harvestLevel() <= toolStats.getHarvestLevel())
                .map(LootTable::dropChanceMap).toList();
        itemMaps.forEach(floatITCItemMap -> floatITCItemMap.forEach(itemMultiMap::put));
        return itemMultiMap;
    }

    /**
     * あるマテリアルをあるツールで掘るために必要な最小硬度を取得
     */
    @ParametersAreNonnullByDefault
    public static int getMinHardness(Material material, IToolStatsModifier toolStatsMod) {
        IToolStatsModifier.ToolStats toolStats = toolStatsMod.apply(null, null);
        List<Integer> hardnessList = Arrays.stream(LootTables.values())
                .map(LootTables::get)
                .filter(table1 -> table1.material().contains(material))
                .filter(table1 -> table1.toolType() == toolStats.getToolType())
                .map(LootTable::harvestLevel).toList();
        if (hardnessList.isEmpty())
            return Integer.MAX_VALUE;

        return Collections.min(hardnessList);
    }
}
