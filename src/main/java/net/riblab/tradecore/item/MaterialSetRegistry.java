/*
 * Copyright (c) 2023. RIBLaB
 */
package net.riblab.tradecore.item;

import net.riblab.tradecore.craft.ITCCraftingRecipe;
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
}
