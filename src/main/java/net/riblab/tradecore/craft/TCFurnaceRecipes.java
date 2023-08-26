package net.riblab.tradecore.craft;

import lombok.Getter;
import net.riblab.tradecore.item.TCItems;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

/**
 * 精錬レシピレジストリ
 */
public enum TCFurnaceRecipes {
    TORCH(new TCFurnaceRecipe(Map.of(TCItems.STICK.get(), 1), new ItemStack(Material.TORCH), 1, 1)),
    ASH(new TCFurnaceRecipe(Map.of(TCItems.ROUND_TRUNK.get(), 1), TCItems.ASH.get().getItemStack(), 1, 1)),
    IRON_SHARD(new TCFurnaceRecipe(Map.of(TCItems.METEORIC_IRON_ORE.get(), 1, TCItems.ASH.get(), 1), TCItems.IRON_SHARD.get().getItemStack(), 1, 3)),
    GOLD_SHARD(new TCFurnaceRecipe(Map.of(TCItems.SANDGOLD.get(), 1, TCItems.ASH.get(), 1), TCItems.GOLD_SHARD.get().getItemStack(), 1, 3));

    @Getter
    private final ITCFurnaceRecipe recipe;

    TCFurnaceRecipes(ITCFurnaceRecipe recipe) {
        this.recipe = recipe;
    }
}
