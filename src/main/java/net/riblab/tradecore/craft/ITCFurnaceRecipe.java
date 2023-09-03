package net.riblab.tradecore.craft;

import net.riblab.tradecore.item.base.ITCItem;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import java.util.Map;

public interface ITCFurnaceRecipe {
    @Nonnull
    Map<ITCItem, Integer> ingredients();

    @Nonnull
    ITCItem result();

    int resultAmount();

    int fuelAmount();
}
