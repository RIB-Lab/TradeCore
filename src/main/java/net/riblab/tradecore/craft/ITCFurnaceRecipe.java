package net.riblab.tradecore.craft;

import org.bukkit.inventory.ItemStack;

import java.util.Map;

public interface ITCFurnaceRecipe {
    Map<net.riblab.tradecore.item.base.ITCItem, Integer> getIngredients();

    ItemStack getResult();

    int getResultAmount();

    int getFuelAmount();
}
