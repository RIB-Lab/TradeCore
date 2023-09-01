package net.riblab.tradecore.craft;

import net.riblab.tradecore.craft.TCCraftingRecipes.RecipeType;
import net.riblab.tradecore.item.base.ITCItem;

import javax.annotation.Nonnull;
import java.util.Map;

public interface ITCCraftingRecipe {

    @Nonnull
    Map<ITCItem, Integer> ingredients();

    @Nonnull
    ITCItem result();

    int resultAmount();

    double fee();

    @Nonnull
    RecipeType category();
}
