/*
 * Copyright (c) 2023. RIBLaB
 */
package net.riblab.tradecore.item;

import net.riblab.tradecore.craft.ITCCraftingRecipe;
import net.riblab.tradecore.item.base.ITCItem;
import org.bukkit.Bukkit;
import org.bukkit.Material;

import java.util.*;

public enum MaterialSetRegistry {
    INSTANCE;

    private final Map<String, Set<Material>> deserializedMaterialSets = new HashMap<>();

    public void clear() {
        deserializedMaterialSets.clear();
    }
    
    public void putAll(Map<String, Set<Material>> materialSets) {
        deserializedMaterialSets.putAll(materialSets);
    }

    /**
     * 変更不可なマテリアルレジストリのコピーを渡す
     *
     * @return
     */
    public Map<String, Set<Material>> getItems() {
        return Collections.unmodifiableMap(deserializedMaterialSets);
    }

    public Optional<Set<Material>> commandToMaterialSet(String setKey) {
        return Optional.ofNullable(deserializedMaterialSets.get(setKey));
    }
}
