/*
 * Copyright (c) 2023. RIBLaB
 */
package net.riblab.tradecore.item;

import net.riblab.tradecore.general.IRegistry;
import org.bukkit.Material;

import java.util.*;

/**
 * 複数のマテリアルで構成されるパターンを管理する
 */
public enum MaterialSetRegistry implements IRegistry<Map<String, Set<Material>>> {
    INSTANCE;

    private final Map<String, Set<Material>> deserializedMaterialSets = new HashMap<>();

    public void clear() {
        deserializedMaterialSets.clear();
    }
    
    public void addAll(Map<String, Set<Material>> materialSets) {
        deserializedMaterialSets.putAll(materialSets);
    }

    @Override
    public Map<String, Set<Material>> getUnmodifiableElements() {
        return Collections.unmodifiableMap(deserializedMaterialSets);
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
