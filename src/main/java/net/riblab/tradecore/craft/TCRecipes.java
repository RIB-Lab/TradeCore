package net.riblab.tradecore.craft;

import lombok.Getter;
import net.riblab.tradecore.item.TCItems;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * レシピ登録システム
 */
public enum TCRecipes {
    //木器時代
    WOODEN_AXE(new TCRecipe(Map.of(TCItems.ROUND_TRUNK.get(), 3, TCItems.STICK.get(), 2), TCItems.WOODEN_AXE.get().getItemStack(), 1, 1, RecipeType.TOOL)),
    WOODEN_SHOVEL(new TCRecipe(Map.of(TCItems.ROUND_TRUNK.get(), 1, TCItems.STICK.get(), 2), TCItems.WOODEN_SHOVEL.get().getItemStack(), 1, 1, RecipeType.TOOL)),
    WOODEN_PICKAXE(new TCRecipe(Map.of(TCItems.ROUND_TRUNK.get(), 3, TCItems.STICK.get(), 2), TCItems.WOODEN_PICKAXE.get().getItemStack(), 1, 1, RecipeType.TOOL)),
    WOODEN_HOE(new TCRecipe(Map.of(TCItems.ROUND_TRUNK.get(), 2, TCItems.STICK.get(), 2), TCItems.WOODEN_HOE.get().getItemStack(), 1, 1, RecipeType.TOOL)),
    WOODEN_SWORD(new TCRecipe(Map.of(TCItems.ROUND_TRUNK.get(), 2, TCItems.STICK.get(), 1), TCItems.WOODEN_SWORD.get().getItemStack(), 1, 1, RecipeType.WEAPON)),
    STICK(new TCRecipe(Map.of(TCItems.TWIG.get(), 4), TCItems.STICK.get().getItemStack(), 1, 1, RecipeType.MISC)),
    CHEST(new TCRecipe(Map.of(TCItems.ROUND_TRUNK.get(), 8), new ItemStack(Material.CHEST), 1, 3, RecipeType.MISC)),
    PLANK(new TCRecipe(Map.of(TCItems.ROUND_TRUNK.get(), 1), new ItemStack(Material.OAK_PLANKS), 1, 1, RecipeType.MISC)),
    WOODEN_COMPONENT(new TCRecipe(Map.of(TCItems.ROUND_TRUNK.get(), 3, TCItems.DUST.get(), 3, TCItems.WOODPULP.get(), 5, TCItems.MOSS.get(), 10), TCItems.WOODEN_COMPONENT.get().getItemStack(), 1, 5, RecipeType.MISC)),
    BARK_HELMET(new TCRecipe(Map.of(TCItems.BARK.get(), 5, TCItems.MUD.get(), 5), TCItems.BARK_HELMET.get().getItemStack(), 1, 1, RecipeType.ARMOR)),
    BARK_CHESTPLATE(new TCRecipe(Map.of(TCItems.BARK.get(), 8, TCItems.MUD.get(), 8), TCItems.BARK_CHESTPLATE.get().getItemStack(), 1, 1, RecipeType.ARMOR)),
    BARK_LEGGINGS(new TCRecipe(Map.of(TCItems.BARK.get(), 7, TCItems.MUD.get(), 7), TCItems.BARK_LEGGINGS.get().getItemStack(), 1, 1, RecipeType.ARMOR)),
    BARK_BOOTS(new TCRecipe(Map.of(TCItems.BARK.get(), 4, TCItems.MUD.get(), 4), TCItems.BARK_BOOTS.get().getItemStack(), 1, 1, RecipeType.ARMOR)),

    //石器時代
    STONE_AXE(new TCRecipe(Map.of(TCItems.WIDESTONE.get(), 1, TCItems.TALLSTONE.get(), 2, TCItems.WOODEN_COMPONENT.get(), 1, TCItems.STICK.get(), 2), TCItems.STONE_AXE.get().getItemStack(), 1, 3, RecipeType.TOOL)),
    STONE_SHOVEL(new TCRecipe(Map.of(TCItems.TALLSTONE.get(), 1, TCItems.WOODEN_COMPONENT.get(), 1, TCItems.STICK.get(), 2), TCItems.STONE_SHOVEL.get().getItemStack(), 1, 3, RecipeType.TOOL)),
    STONE_PICKAXE(new TCRecipe(Map.of(TCItems.WIDESTONE.get(), 3, TCItems.WOODEN_COMPONENT.get(), 1, TCItems.STICK.get(), 2), TCItems.STONE_PICKAXE.get().getItemStack(), 1, 3, RecipeType.TOOL)),
    STONE_HOE(new TCRecipe(Map.of(TCItems.WIDESTONE.get(), 2, TCItems.WOODEN_COMPONENT.get(), 1, TCItems.STICK.get(), 2), TCItems.STONE_HOE.get().getItemStack(), 1, 3, RecipeType.TOOL)),
    STONE_SWORD(new TCRecipe(Map.of(TCItems.TALLSTONE.get(), 2, TCItems.WOODEN_COMPONENT.get(), 1, TCItems.STICK.get(), 2), TCItems.STONE_SWORD.get().getItemStack(), 1, 3, RecipeType.WEAPON)),
    FURNACE(new TCRecipe(Map.of(TCItems.ROUND_STONE.get(), 3, TCItems.STONE_PICKAXE.get(), 1), new ItemStack(Material.FURNACE), 1, 10, RecipeType.MISC)),
    FUEL_BALL(new TCRecipe(Map.of(TCItems.DRYGRASS.get(), 32), TCItems.FUEL_BALL.get().getItemStack(), 1, 1, RecipeType.MISC)),
    STONE_HELMET(new TCRecipe(Map.of(TCItems.TALLSTONE.get(), 10, TCItems.WIDESTONE.get(), 15, TCItems.WOODEN_COMPONENT.get(), 1), TCItems.STONE_HELMET.get().getItemStack(), 1, 3, RecipeType.ARMOR)),
    STONE_CHESTPLATE(new TCRecipe(Map.of(TCItems.TALLSTONE.get(), 10, TCItems.WIDESTONE.get(), 30, TCItems.WOODEN_COMPONENT.get(), 1), TCItems.STONE_CHESTPLATE.get().getItemStack(), 1, 3, RecipeType.ARMOR)),
    STONE_LEGGINGS(new TCRecipe(Map.of(TCItems.TALLSTONE.get(), 30, TCItems.WIDESTONE.get(), 5, TCItems.WOODEN_COMPONENT.get(), 1), TCItems.STONE_LEGGINGS.get().getItemStack(), 1, 3, RecipeType.ARMOR)),
    STONE_BOOTS(new TCRecipe(Map.of(TCItems.TALLSTONE.get(), 40, TCItems.WOODEN_COMPONENT.get(), 1), TCItems.STONE_BOOTS.get().getItemStack(), 1, 3, RecipeType.ARMOR)),
    STONE_COMPONENT(new TCRecipe(Map.of(TCItems.ROUND_STONE.get(), 10, TCItems.ANDESITE_STONE.get(), 10, TCItems.GRANITE_STONE.get(), 10, TCItems.DIORITE_STONE.get(), 10), TCItems.STONE_COMPONENT.get().getItemStack(), 1, 10, RecipeType.MISC));

    @Getter
    private final TCRecipe recipe;

    TCRecipes(TCRecipe recipe) {
        this.recipe = recipe;
    }

    /**
     * ある種類のレシピを全て取得する
     */
    public static List<TCRecipe> getRecipes(RecipeType type) {
        return Arrays.stream(TCRecipes.values()).map(TCRecipes::getRecipe).filter(tcRecipe -> tcRecipe.getCategory() == type).collect(Collectors.toList());
    }

    /**
     * レシピの種類
     */
    public enum RecipeType {
        ARMOR, TOOL, WEAPON, MISC;
    }
}