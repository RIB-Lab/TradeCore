package net.riblab.tradecore.craft;

import net.riblab.tradecore.TradeCore;
import net.riblab.tradecore.item.base.TCItems;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapelessRecipe;

/**
 * バニラレシピの削除・登録クラス
 */
public final class VanillaCraftInitializer {
    private VanillaCraftInitializer() {

    }

    public static void init() {
        Bukkit.clearRecipes();

        ItemStack hatchet = TCItems.HATCHET.get().getItemStack();
        ShapelessRecipe hatchetRecipe = new ShapelessRecipe(new NamespacedKey(TradeCore.getInstance(), TCItems.HATCHET.get().getInternalName()), hatchet);
        hatchetRecipe.addIngredient(TCItems.PEBBLE.get().getItemStack());
        hatchetRecipe.addIngredient(TCItems.STICK.get().getItemStack());
        Bukkit.getServer().addRecipe(hatchetRecipe);

        ItemStack crafting_table = new ItemStack(Material.CRAFTING_TABLE);
        ShapelessRecipe crafting_table_recipe = new ShapelessRecipe(new NamespacedKey(TradeCore.getInstance(), crafting_table.getType().toString().toLowerCase()), crafting_table);
        ItemStack ingredient = TCItems.ROUND_TRUNK.get().getItemStack();
        ingredient.setAmount(4);
        crafting_table_recipe.addIngredient(ingredient);
        Bukkit.getServer().addRecipe(crafting_table_recipe);
    }
}
