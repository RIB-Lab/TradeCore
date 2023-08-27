package net.riblab.tradecore.craft;

import net.riblab.tradecore.item.base.ITCItem;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

public interface ITCFurnaceRecipe {
    Map<ITCItem, Integer> getIngredients();

    ItemStack getResult();

    int getResultAmount();

    int getFuelAmount();
}
