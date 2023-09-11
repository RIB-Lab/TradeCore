/*
 * Copyright (c) 2023. RIBLaB
 */
package net.riblab.tradecore.item.mod;

import net.riblab.tradecore.dungeon.DungeonDatas;
import net.riblab.tradecore.modifier.IEnterDungeonModifier;

import java.util.Optional;

public class ModDungeonMapI extends ItemMod<String> implements IEnterDungeonModifier {
    public ModDungeonMapI(String name) {
        super(name);
    }

    @Override
    public Optional<String> getLore() {
        return Optional.of("「" + DungeonDatas.internalNameToDungeonData(this.getParam()).orElseThrow().getName() + "」に入場できる");
    }

    @Override
    public String apply(String originalValue, String modifiedValue) {
        return getParam();
    }
}
