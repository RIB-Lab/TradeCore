package net.riblab.tradecore.craft;

import net.riblab.tradecore.craft.TCCraftingRecipes.RecipeType;
import net.riblab.tradecore.item.base.ITCItem;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

public interface ITCCraftingRecipe {
    Map<ITCItem, Integer> getIngredients();

    ItemStack getResult();

    int getResultAmount();

    double getFee();

    RecipeType getCategory();
}
