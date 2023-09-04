package net.riblab.tradecore.craft;

import net.riblab.tradecore.item.base.ITCItem;

import javax.annotation.Nonnull;
import java.util.Map;

public sealed interface ITCFurnaceRecipe permits TCFurnaceRecipe {
    @Nonnull
    Map<ITCItem, Integer> ingredients();

    @Nonnull
    ITCItem result();

    int resultAmount();

    int fuelAmount();
}
