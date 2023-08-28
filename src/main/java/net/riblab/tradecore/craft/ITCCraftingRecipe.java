package net.riblab.tradecore.craft;

import net.riblab.tradecore.craft.TCCraftingRecipes.RecipeType;
import net.riblab.tradecore.item.base.ITCItem;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import java.util.Map;

public interface ITCCraftingRecipe {

    @Nonnull Map<ITCItem, Integer> getIngredients();

    @Nonnull ItemStack getResult();

    int getResultAmount();

    double getFee();

    @Nonnull RecipeType getCategory();
}
