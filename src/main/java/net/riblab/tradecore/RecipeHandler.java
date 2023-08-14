package net.riblab.tradecore;

import net.riblab.tradecore.item.TCItems;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapelessRecipe;

public class RecipeHandler {
    public RecipeHandler(){
        Bukkit.clearRecipes();
        
        //TODO:システム化
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
