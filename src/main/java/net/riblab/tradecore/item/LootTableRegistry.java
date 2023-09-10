/*
 * Copyright (c) 2023. RIBLaB
 */
package net.riblab.tradecore.item;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import net.riblab.tradecore.general.IRegistry;
import net.riblab.tradecore.modifier.IToolStatsModifier;
import org.bukkit.Material;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.*;

/**
 * ルートテーブルの管理機構
 */
public enum LootTableRegistry implements IRegistry<Map<String, ILootTable>> {
    INSTANCE;

    private final Map<String, ILootTable> lootTables = new HashMap<>();

    @Override
    public void clear() {
        lootTables.clear();
    }

    @Override
    public void addAll(Map<String, ILootTable> lootTables) {
        this.lootTables.putAll(lootTables);
    }

    @Override
    public Map<String, ILootTable> getUnmodifiableElements() {
        return Collections.unmodifiableMap(lootTables);
    }


    /**
     * マテリアルとツールタイプに一致するルートテーブルを洗い出す
     *
     * @return ルートテーブル
     */
    @ParametersAreNonnullByDefault
    public Multimap<Float, String> get(Material material, IToolStatsModifier.ToolType toolType) {
        Multimap<Float, String> itemMultiMap = ArrayListMultimap.create();
        List<Map<String, Float>> itemMaps = LootTableRegistry.INSTANCE.getUnmodifiableElements().values().stream()
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
    public Multimap<Float, String> get(Material material, IToolStatsModifier toolStatsMod) {
        IToolStatsModifier.ToolStats toolStats = toolStatsMod.apply(null, null);
        Multimap<Float, String> itemMultiMap = ArrayListMultimap.create();
        List<Map<String, Float>> itemMaps = LootTableRegistry.INSTANCE.getUnmodifiableElements().values().stream()
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
    public int getMinHardness(Material material, IToolStatsModifier toolStatsMod) {
        IToolStatsModifier.ToolStats toolStats = toolStatsMod.apply(null, null);
        List<Integer> hardnessList = LootTableRegistry.INSTANCE.getUnmodifiableElements().values().stream()
                .filter(table1 -> MaterialSetRegistry.INSTANCE.commandToMaterialSet(table1.getMaterialSetKey()).orElseThrow().contains(material))
                .filter(table1 -> table1.getToolType() == toolStats.getToolType())
                .map(ILootTable::getHarvestLevel).toList();
        if (hardnessList.isEmpty())
            return Integer.MAX_VALUE;

        return Collections.min(hardnessList);
    }

    public Optional<ILootTable> commandToLootTable(String internalName) {
        return Optional.ofNullable(LootTableRegistry.INSTANCE.getUnmodifiableElements().get(internalName));
    }
}
