package net.riblab.tradecore.craft;

public interface ITCCraftingRecipe {
    java.util.Map<net.riblab.tradecore.item.attribute.ITCItem, Integer> getIngredients();

    org.bukkit.inventory.ItemStack getResult();

    int getResultAmount();

    double getFee();

    TCCraftingRecipes.RecipeType getCategory();
}
