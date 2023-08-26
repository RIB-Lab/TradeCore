package net.riblab.tradecore.craft;

public interface ITCFurnaceRecipe {
    java.util.Map<net.riblab.tradecore.item.attribute.ITCItem, Integer> getIngredients();

    org.bukkit.inventory.ItemStack getResult();

    int getResultAmount();

    int getFuelAmount();
}
