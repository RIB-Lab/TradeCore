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
    GRASS(new LootTable("grasses", IToolStatsModifier.ToolType.HAND, 0, Map.of("drygrass", 1f, "pebble", 0.2f))),
    LEAVES(new LootTable("leaves", IToolStatsModifier.ToolType.AXE, 0, Map.of("stick", 0.2f))),
    LEAVES2(new LootTable("leaves", IToolStatsModifier.ToolType.AXE, 1, Map.of("stick", 0.1f))),
    LOGS(new LootTable("logs", IToolStatsModifier.ToolType.AXE, 1, Map.of("round_trunk", 0.2f, "bark", 0.4f,  "twig", 0.8f))),
    LOGS2(new LootTable("logs", IToolStatsModifier.ToolType.AXE, 2, Map.of("round_trunk", 0.2f))),
    LOGS3(new LootTable("logs", IToolStatsModifier.ToolType.AXE, 3, Map.of("round_trunk", 0.1f))),
    DIRTS(new LootTable("dirts", IToolStatsModifier.ToolType.SHOVEL, 0, Map.of("dust", 0.2f, "mud", 0.1f))),
    DIRTS2(new LootTable("dirts", IToolStatsModifier.ToolType.SHOVEL, 1, Map.of("dust", 0.1f, "mud", 0.05f))),
    PRIMITIVE_STONES(new LootTable("primitive_stones", IToolStatsModifier.ToolType.PICKAXE, 0, Map.of("widestone", 0.2f, "tallstone", 0.2f))),
    PRIMITIVE_STONES2(new LootTable("primitive_stones", IToolStatsModifier.ToolType.PICKAXE, 1, Map.of("round_stone", 0.1f))),
    PRIMITIVE_PLANKS(new LootTable("primitive_stones", IToolStatsModifier.ToolType.AXE, 2, Map.of("woodpulp", 1.0f))),
    MOSS(new LootTable("farmland", IToolStatsModifier.ToolType.HOE, 0, Map.of("moss", 0.2f, "drygrass", 1.0f))),
    MOSS2(new LootTable("farmland", IToolStatsModifier.ToolType.HOE, 0, Map.of("moss", 0.1f, "drygrass", 0.5f))),
    ANDESITE(new LootTable("andesite", IToolStatsModifier.ToolType.PICKAXE, 1, Map.of("andesite_stone", 0.2f))),
    GRANITE(new LootTable("granite", IToolStatsModifier.ToolType.PICKAXE, 1, Map.of("granite_stone", 0.2f))),
    DIORITE(new LootTable("diorite", IToolStatsModifier.ToolType.PICKAXE, 1, Map.of("diorite_stone", 0.2f))),
    GRAVEL(new LootTable("gravel", IToolStatsModifier.ToolType.SHOVEL, 1, Map.of("gravel_dust", 0.2f, "flint", 0.02f, "meteoric_iron_ore", 0.02f))),
    SAND(new LootTable("sand", IToolStatsModifier.ToolType.SHOVEL, 1, Map.of("sand_dust", 0.2f, "sandgold", 0.02f)));

    private final ILootTable table;

    LootTables(ILootTable table) {
        this.table = table;
    }

    public ILootTable get() {
        return table;
    }

    /**
     * マテリアルとツールタイプに一致するルートテーブルを洗い出す
     *
     * @return ルートテーブル
     */
    @ParametersAreNonnullByDefault
    public static Multimap<Float, String> get(Material material, IToolStatsModifier.ToolType toolType) {
        Multimap<Float, String> itemMultiMap = ArrayListMultimap.create();
        List<Map<String, Float>> itemMaps = Arrays.stream(LootTables.values())
                .map(LootTables::get)
                .filter(table1 -> MaterialSetRegistry.INSTANCE.commandToMaterialSet(table1.getMaterialSetKey()).orElseThrow().contains(material))
                .filter(table1 -> table1.getToolType() == toolType)
                .map(ILootTable::getDropChanceMap).toList();
        itemMaps.forEach(floatITCItemMap -> floatITCItemMap.forEach((s, aFloat) -> itemMultiMap.put(aFloat, s)));
        return itemMultiMap;
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
        List<Map<String, Float>> itemMaps = Arrays.stream(LootTables.values())
                .map(LootTables::get)
                .filter(table1 -> MaterialSetRegistry.INSTANCE.commandToMaterialSet(table1.getMaterialSetKey()).orElseThrow().contains(material))
                .filter(table1 -> table1.getToolType() == toolStats.getToolType())
                .filter(ILootTable -> ILootTable.getHarvestLevel() <= toolStats.getHarvestLevel())
                .map(ILootTable::getDropChanceMap).toList();
        itemMaps.forEach(floatITCItemMap -> floatITCItemMap.forEach((s, aFloat) -> itemMultiMap.put(aFloat, s)));
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
                .filter(table1 -> MaterialSetRegistry.INSTANCE.commandToMaterialSet(table1.getMaterialSetKey()).orElseThrow().contains(material))
                .filter(table1 -> table1.getToolType() == toolStats.getToolType())
                .map(ILootTable::getHarvestLevel).toList();
        if (hardnessList.isEmpty())
            return Integer.MAX_VALUE;

        return Collections.min(hardnessList);
    }
}
