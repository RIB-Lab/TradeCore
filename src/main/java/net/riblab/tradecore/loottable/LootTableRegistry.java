/*
 * Copyright (c) 2023. RIBLaB
 */
package net.riblab.tradecore.loottable;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import net.riblab.tradecore.general.ChanceFloat;
import net.riblab.tradecore.general.IRegistry;
import net.riblab.tradecore.item.MaterialSetRegistry;
import net.riblab.tradecore.item.base.TCItemRegistry;
import net.riblab.tradecore.modifier.IToolStatsModifier;
import org.bukkit.Location;
import org.bukkit.Material;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.*;

/**
 * ルートテーブルの管理機構
 */
public enum LootTableRegistry implements IRegistry<Map<String, ILootTable>> {
    INSTANCE;

    private final Map<String, ILootTable> lootTables = new HashMap<>();
    
    private final Map<UUID, Integer> cachedMinHardness = new HashMap<>();

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
     * マテリアル、ツールタイプ、採掘レベル、その他ルートテーブルmod達の条件を全て満たすルートテーブルのドロップアイテムを洗い出す。全部のルートテーブルを走査するので慎重に
     *
     * @return ルートテーブル
     */
    @ParametersAreNonnullByDefault
    public Multimap<Float, String> get(Material material, IToolStatsModifier toolStatsMod, Location blockLoc) {
        IToolStatsModifier.ToolStats toolStats = toolStatsMod.apply(null, null);
        Multimap<Float, String> itemMultiMap = ArrayListMultimap.create();
        List<ILootTable> lootTableCandidates = LootTableRegistry.INSTANCE.getUnmodifiableElements().values().stream()
                .filter(table1 -> MaterialSetRegistry.INSTANCE.commandToMaterialSet(table1.getMaterialSetKey()).orElseThrow().contains(material))
                .filter(table1 -> table1.getToolType() == toolStats.getToolType())
                .filter(ILootTable -> ILootTable.getHarvestLevel() <= toolStats.getHarvestLevel())
                .toList();

        //mod検査ゾーン
        List<Map<String, ChanceFloat>> itemMaps = new ArrayList<>();
        for (ILootTable lootTable : lootTableCandidates) {
            Optional<ILootTableMod<?>> maxHeightmod = lootTable.getMods().stream().filter(iLootTableMod -> iLootTableMod instanceof ModMaxHeight).findFirst();
            if(maxHeightmod.isPresent()){
                if(((ModMaxHeight) maxHeightmod.get()).getParam() < blockLoc.getY()){
                    continue; //最大採掘可能高度　< 現在の採掘高度 -> X
                }
            }

            Optional<ILootTableMod<?>> minHeightmod = lootTable.getMods().stream().filter(iLootTableMod -> iLootTableMod instanceof ModMinHeight).findFirst();
            if(minHeightmod.isPresent()){
                if(((ModMinHeight) minHeightmod.get()).getParam() > blockLoc.getY()){
                    continue; //最小採掘可能高度　> 現在の採掘高度 -> X
                }
            }

            itemMaps.add(lootTable.getDropChanceMap());
        }
        
        itemMaps.forEach(floatITCItemMap -> floatITCItemMap.forEach((s, aFloat) -> itemMultiMap.put(aFloat.get(), s)));
        return itemMultiMap;
    }

    /**
     * あるマテリアルをあるツールで掘るために必要な最小硬度を取得。全部のルートテーブルを走査するので慎重に
     */
    @ParametersAreNonnullByDefault
    public int getMinHardness(Material material, IToolStatsModifier toolStatsMod, Location blockLoc) {
        IToolStatsModifier.ToolStats toolStats = toolStatsMod.apply(null, null);
        
        List<ILootTable> lootTableCandidates = LootTableRegistry.INSTANCE.getUnmodifiableElements().values().stream()
                .filter(table1 -> MaterialSetRegistry.INSTANCE.commandToMaterialSet(table1.getMaterialSetKey()).orElseThrow().contains(material))
                .filter(table1 -> table1.getToolType() == toolStats.getToolType())
                .toList(); //マテリアルセットとツールの条件を満たしたルートテーブル達
        
        //mod検査ゾーン
        List<Integer> hardnessList = new ArrayList<>();
        for (ILootTable lootTable : lootTableCandidates) {
            Optional<ILootTableMod<?>> maxHeightmod = lootTable.getMods().stream().filter(iLootTableMod -> iLootTableMod instanceof ModMaxHeight).findFirst();
            if(maxHeightmod.isPresent()){
                if(((ModMaxHeight) maxHeightmod.get()).getParam() < blockLoc.getY()){
                    continue; //最大採掘可能高度　< 現在の採掘高度 -> X
                }
            }

            Optional<ILootTableMod<?>> minHeightmod = lootTable.getMods().stream().filter(iLootTableMod -> iLootTableMod instanceof ModMinHeight).findFirst();
            if(minHeightmod.isPresent()){
                if(((ModMinHeight) minHeightmod.get()).getParam() > blockLoc.getY()){
                    continue; //最小採掘可能高度　> 現在の採掘高度 -> X
                }
            }

            hardnessList.add(lootTable.getHarvestLevel());
        }
        
        if (hardnessList.isEmpty())
            return Integer.MAX_VALUE;

        return Collections.min(hardnessList);
    }

    /**
     * あるマテリアルをあるツールで掘るために必要な最小硬度を取得。<br>
     * 走査処理が重いので、掘っているプレイヤーのキャッシュがあればそちらを優先する
     */
    @ParametersAreNonnullByDefault
    public int getMinHardness(UUID player, Material material, IToolStatsModifier toolStatsMod, Location blockLoc) {
        Integer cachedHardness = cachedMinHardness.get(player);
        if(cachedHardness != null){
            return cachedHardness;
        }
        
        int minHardness = getMinHardness(material, toolStatsMod, blockLoc);
        cachedMinHardness.put(player, minHardness);
        
        return minHardness;
    }

    /**
     *  getCachedMinHardnessで使うキャッシュを削除する。<br>
     *  あるプレイヤーが新しいブロックを掘り始めた時呼ぶ
     */
    public void clearCachedMinHardness(UUID player){
        cachedMinHardness.remove(player);
    }

    public Optional<ILootTable> commandToLootTable(String internalName) {
        return Optional.ofNullable(LootTableRegistry.INSTANCE.getUnmodifiableElements().get(internalName));
    }

    /**
     * ルートテーブルがちゃんと成立しているか確認する
     */
    public void validate() {
        for (Map.Entry<String, ILootTable> lootTableEntry : lootTables.entrySet()) {
            MaterialSetRegistry.INSTANCE.commandToMaterialSet(lootTableEntry.getValue().getMaterialSetKey()).orElseThrow(
                    () -> new NoSuchElementException("ルートテーブル" + lootTableEntry.getKey() + "のマテリアルセット" + lootTableEntry.getValue().getMaterialSetKey() + "がレジストリに見つかりません。"));
            lootTableEntry.getValue().getDropChanceMap().forEach((itemStr, aFloat) -> {
                TCItemRegistry.INSTANCE.commandToTCItem(itemStr).orElseThrow(
                        () -> new NoSuchElementException("ルートテーブル" + lootTableEntry.getKey() + "のアイテム" + itemStr + "がアイテムレジストリに見つかりません。"));
            });
        }
    }
}
