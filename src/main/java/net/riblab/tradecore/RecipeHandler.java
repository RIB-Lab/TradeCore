package net.riblab.tradecore;

import net.kyori.adventure.key.Key;
import net.riblab.tradecore.item.TCItems;
import org.bukkit.Bukkit;
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
    }
}
