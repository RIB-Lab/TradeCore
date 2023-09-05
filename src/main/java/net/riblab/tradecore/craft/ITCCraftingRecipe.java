package net.riblab.tradecore.craft;

import net.riblab.tradecore.craft.TCCraftingRecipes.RecipeType;
import net.riblab.tradecore.item.base.ITCItem;

import javax.annotation.Nonnull;
import java.util.Map;

public sealed interface ITCCraftingRecipe permits TCCraftingRecipe {

    @Nonnull
    Map<String, Integer> ingredients();

    @Nonnull
    String result();

    int resultAmount();

    double fee();

    @Nonnull
    RecipeType category();
}
