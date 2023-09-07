/*
 * Copyright (c) 2023. RIBLaB
 */
package net.riblab.tradecore.item.mod;

import net.riblab.tradecore.dungeon.DungeonNames;
import net.riblab.tradecore.modifier.IEnterDungeonModifier;

import java.util.Optional;

public class ModDungeonMapI extends ItemMod<DungeonNames> implements IEnterDungeonModifier {
    public ModDungeonMapI(DungeonNames name) {
        super(name);
    }

    @Override
    public Optional<String> getLore() {
        return Optional.of("「" + this.getParam().getDisplayName() + "」に入場できる");
    }

    @Override
    public DungeonNames apply(DungeonNames originalValue, DungeonNames modifiedValue) {
        return getParam();
    }
}
