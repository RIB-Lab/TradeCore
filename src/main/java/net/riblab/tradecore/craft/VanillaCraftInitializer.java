package net.riblab.tradecore.craft;

import net.riblab.tradecore.item.base.TCItems;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * バニラレシピの削除・登録クラス
 */
public enum VanillaCraftInitializer {
    INSTANCE;

    public void init(JavaPlugin plugin) {
        Bukkit.clearRecipes();

        ItemStack hatchet = TCItems.HATCHET.get().getTemplateItemStack();
        ShapelessRecipe hatchetRecipe = new ShapelessRecipe(new NamespacedKey(plugin, TCItems.HATCHET.get().getInternalName()), hatchet);
        hatchetRecipe.addIngredient(TCItems.PEBBLE.get().getTemplateItemStack());
        hatchetRecipe.addIngredient(TCItems.STICK.get().getTemplateItemStack());
        Bukkit.getServer().addRecipe(hatchetRecipe);

        ItemStack crafting_table = new ItemStack(Material.CRAFTING_TABLE);
        ShapelessRecipe crafting_table_recipe = new ShapelessRecipe(new NamespacedKey(plugin, crafting_table.getType().toString().toLowerCase()), crafting_table);
        ItemStack ingredient = TCItems.ROUND_TRUNK.get().getTemplateItemStack();
        ingredient.setAmount(4);
        crafting_table_recipe.addIngredient(ingredient);
        Bukkit.getServer().addRecipe(crafting_table_recipe);
    }
}
