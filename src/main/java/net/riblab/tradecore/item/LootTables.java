/*
 * Copyright (c) 2023. RIBLaB
 */
package net.riblab.tradecore.item;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import net.riblab.tradecore.item.base.ITCItem;
import net.riblab.tradecore.item.base.TCItems;
import net.riblab.tradecore.modifier.IToolStatsModifier;
import org.bukkit.Material;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.*;

/**
 * ブロックを壊したとき落ちるアイテムを記述したルートテーブルの一覧
 */
public enum LootTables {
    GRASS(new LootTable(Set.of(Material.GRASS, Material.TALL_GRASS), IToolStatsModifier.ToolType.HAND, 0, Map.of(1f, TCItems.DRYGRASS.get(), 0.2f, TCItems.PEBBLE.get()))),
    LEAVES(new LootTable(Materials.LEAVES.get(), IToolStatsModifier.ToolType.AXE, 0, Map.of(0.2f, TCItems.STICK.get()))),
    LEAVES2(new LootTable(Materials.LEAVES.get(), IToolStatsModifier.ToolType.AXE, 1, Map.of(0.1f, TCItems.STICK.get()))),
    LOGS(new LootTable(Materials.LOGS.get(), IToolStatsModifier.ToolType.AXE, 1, Map.of(0.2f, TCItems.ROUND_TRUNK.get(), 0.4f, TCItems.BARK.get(), 0.8f, TCItems.TWIG.get()))),
    LOGS2(new LootTable(Materials.LOGS.get(), IToolStatsModifier.ToolType.AXE, 2, Map.of(0.2f, TCItems.ROUND_TRUNK.get()))),
    LOGS3(new LootTable(Materials.LOGS.get(), IToolStatsModifier.ToolType.AXE, 3, Map.of(0.1f, TCItems.ROUND_TRUNK.get()))),
    DIRTS(new LootTable(Materials.DIRTS.get(), IToolStatsModifier.ToolType.SHOVEL, 0, Map.of(0.2f, TCItems.DUST.get(), 0.1f, TCItems.MUD.get()))),
    DIRTS2(new LootTable(Materials.DIRTS.get(), IToolStatsModifier.ToolType.SHOVEL, 1, Map.of(0.1f, TCItems.DUST.get(), 0.05f, TCItems.MUD.get()))),
    PRIMITIVE_STONES(new LootTable(Materials.PRIMITIVE_STONES.get(), IToolStatsModifier.ToolType.PICKAXE, 0, Map.of(0.2f, TCItems.WIDESTONE.get(), 0.21f, TCItems.TALLSTONE.get()))),
    PRIMITIVE_STONES2(new LootTable(Materials.PRIMITIVE_STONES.get(), IToolStatsModifier.ToolType.PICKAXE, 1, Map.of(0.1f, TCItems.ROUND_STONE.get()))),
    PRIMITIVE_PLANKS(new LootTable(Materials.PRIMITIVE_PLANKS.get(), IToolStatsModifier.ToolType.AXE, 2, Map.of(1.0f, TCItems.WOODPULP.get()))),
    MOSS(new LootTable(Set.of(Material.FARMLAND), IToolStatsModifier.ToolType.HOE, 0, Map.of(0.2f, TCItems.MOSS.get(), 1.0f, TCItems.DRYGRASS.get()))),
    MOSS2(new LootTable(Set.of(Material.FARMLAND), IToolStatsModifier.ToolType.HOE, 0, Map.of(0.1f, TCItems.MOSS.get(), 0.5f, TCItems.DRYGRASS.get()))),
    ANDESITE(new LootTable(Set.of(Material.ANDESITE), IToolStatsModifier.ToolType.PICKAXE, 1, Map.of(0.2f, TCItems.ANDESITE_STONE.get()))),
    GRANITE(new LootTable(Set.of(Material.GRANITE), IToolStatsModifier.ToolType.PICKAXE, 1, Map.of(0.2f, TCItems.GRANITE_STONE.get()))),
    DIORITE(new LootTable(Set.of(Material.DIORITE), IToolStatsModifier.ToolType.PICKAXE, 1, Map.of(0.2f, TCItems.DIORITE_STONE.get()))),
    GRAVEL(new LootTable(Set.of(Material.GRAVEL), IToolStatsModifier.ToolType.SHOVEL, 1, Map.of(0.2f, TCItems.GRAVEL_DUST.get(), 0.02f, TCItems.FLINT.get(), 0.021f, TCItems.METEORIC_IRON_ORE.get()))),
    SAND(new LootTable(Set.of(Material.SAND), IToolStatsModifier.ToolType.SHOVEL, 1, Map.of(0.2f, TCItems.SAND_DUST.get(), 0.02f, TCItems.SANDGOLD.get())));

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
    public static Multimap<Float, ITCItem> get(Material material, IToolStatsModifier.ToolType toolType) {
        Multimap<Float, ITCItem> itemMap = ArrayListMultimap.create();
        List<Map<Float, ITCItem>> itemMaps = Arrays.stream(LootTables.values()).map(LootTables::get).filter(table1 -> table1.material().contains(material)).filter(table1 -> table1.toolType() == toolType)
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
    public static Multimap<Float, ITCItem> get(Material material, IToolStatsModifier toolStatsMod) {
        IToolStatsModifier.ToolStats toolStats = toolStatsMod.apply(null, null);
        Multimap<Float, ITCItem> itemMultiMap = ArrayListMultimap.create();
        List<Map<Float, ITCItem>> itemMaps = Arrays.stream(LootTables.values())
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

    /**
     * バニラのルートテーブルとは別の概念
     *
     * @param material      ドロップテーブルの対象アイテム
     * @param toolType      ドロップテーブルを発動させるのに必要なツールの種類
     * @param harvestLevel  ドロップテーブルを発動させるために必要な最小のツールレベル
     * @param dropChanceMap ドロップ率(0~1)とその確率でドロップするアイテムのマップ
     */
    public record LootTable(Set<Material> material,
                            IToolStatsModifier.ToolType toolType,
                            int harvestLevel,
                            Map<Float, ITCItem> dropChanceMap) {
    }
}
