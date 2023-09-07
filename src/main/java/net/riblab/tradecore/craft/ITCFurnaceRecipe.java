/*
 * Copyright (c) 2023. RIBLaB
 */
package net.riblab.tradecore.craft;

import net.riblab.tradecore.item.base.ITCItem;

import javax.annotation.Nonnull;
import java.util.Map;

public sealed interface ITCFurnaceRecipe permits TCFurnaceRecipe {
    @Nonnull
    Map<String, Integer> ingredients();

    @Nonnull
    String result();

    int resultAmount();

    int fuelAmount();
}
