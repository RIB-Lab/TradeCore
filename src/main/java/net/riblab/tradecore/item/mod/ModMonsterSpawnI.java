/*
 * Copyright (c) 2023. RIBLaB
 */
package net.riblab.tradecore.item.mod;

import net.riblab.tradecore.entity.mob.ITCMob;
import net.riblab.tradecore.entity.mob.TCMobs;
import net.riblab.tradecore.modifier.IMonsterSpawnModifier;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

/**
 * 使用時に確率で敵が沸くツールのmod
 */
public class ModMonsterSpawnI extends ItemMod<Map<String, Float>> implements IMonsterSpawnModifier {

    /**
     * @param spawnTable 沸くモブとその確率
     */
    public ModMonsterSpawnI(Map<String, Float> spawnTable) {
        super(spawnTable);
    }

    @Override
    public Optional<String> getLore() {
        return Optional.empty();
    }

    @Override
    public List<ITCMob> apply(List<ITCMob> originalValue, List<ITCMob> modifiedValue) {
        float rand = new Random().nextFloat();
        getParam().forEach((string, aFloat) -> {
            if (rand < aFloat) {
                TCMobs.commandToTCMob(string).ifPresent(modifiedValue::add);
            }
        });

        return modifiedValue;
    }
}
