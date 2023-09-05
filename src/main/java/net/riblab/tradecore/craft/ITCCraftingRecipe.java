package net.riblab.tradecore.craft;

public interface ITCCraftingRecipe {
    java.util.Map<String, Integer> getIngredients();

    String getResult();

    int getResultAmount();

    double getFee();

    TCCraftingRecipes.RecipeType getCategory();
}
