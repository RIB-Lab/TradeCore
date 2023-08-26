package net.riblab.tradecore.craft;

public interface ITCFurnaceRecipe {
    java.util.Map<net.riblab.tradecore.item.base.ITCItem, Integer> getIngredients();

    org.bukkit.inventory.ItemStack getResult();

    int getResultAmount();

    int getFuelAmount();
}
