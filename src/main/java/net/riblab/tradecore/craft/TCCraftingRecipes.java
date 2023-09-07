/*
 * Copyright (c) 2023. RIBLaB
 */
package net.riblab.tradecore.craft;

import lombok.Getter;
import net.riblab.tradecore.item.base.TCItems;

import java.util.Map;

/**
 * レシピ登録システム
 */
public enum TCCraftingRecipes {
    //木器時代
    WOODEN_AXE(new TCCraftingRecipe(Map.of(TCItems.ROUND_TRUNK.get().getInternalName(), 3, TCItems.STICK.get().getInternalName(), 2), TCItems.WOODEN_AXE.get().getInternalName(), 1, 1, CraftingRecipesRegistry.RecipeType.TOOL)),
    WOODEN_SHOVEL(new TCCraftingRecipe(Map.of(TCItems.ROUND_TRUNK.get().getInternalName(), 1, TCItems.STICK.get().getInternalName(), 2), TCItems.WOODEN_SHOVEL.get().getInternalName(), 1, 1, CraftingRecipesRegistry.RecipeType.TOOL)),
    WOODEN_PICKAXE(new TCCraftingRecipe(Map.of(TCItems.ROUND_TRUNK.get().getInternalName(), 3, TCItems.STICK.get().getInternalName(), 2), TCItems.WOODEN_PICKAXE.get().getInternalName(), 1, 1, CraftingRecipesRegistry.RecipeType.TOOL)),
    WOODEN_HOE(new TCCraftingRecipe(Map.of(TCItems.ROUND_TRUNK.get().getInternalName(), 2, TCItems.STICK.get().getInternalName(), 2), TCItems.WOODEN_HOE.get().getInternalName(), 1, 1, CraftingRecipesRegistry.RecipeType.TOOL)),
    WOODEN_SWORD(new TCCraftingRecipe(Map.of(TCItems.ROUND_TRUNK.get().getInternalName(), 2, TCItems.STICK.get().getInternalName(), 1), TCItems.WOODEN_SWORD.get().getInternalName(), 1, 1, CraftingRecipesRegistry.RecipeType.WEAPON)),
    STICK(new TCCraftingRecipe(Map.of(TCItems.TWIG.get().getInternalName(), 4), TCItems.STICK.get().getInternalName(), 1, 0.25d, CraftingRecipesRegistry.RecipeType.MISC)),
    CHEST(new TCCraftingRecipe(Map.of(TCItems.ROUND_TRUNK.get().getInternalName(), 8), TCItems.VANILLA_CHEST.get().getInternalName(), 1, 3, CraftingRecipesRegistry.RecipeType.MISC)),
    PLANK(new TCCraftingRecipe(Map.of(TCItems.ROUND_TRUNK.get().getInternalName(), 1), TCItems.VANILLA_PLANK.get().getInternalName(), 1, 1, CraftingRecipesRegistry.RecipeType.MISC)),
    WOODEN_COMPONENT(new TCCraftingRecipe(Map.of(TCItems.ROUND_TRUNK.get().getInternalName(), 3, TCItems.DUST.get().getInternalName(), 3, TCItems.WOODPULP.get().getInternalName(), 5, TCItems.MOSS.get().getInternalName(), 10), TCItems.WOODEN_COMPONENT.get().getInternalName(), 1, 5, CraftingRecipesRegistry.RecipeType.MISC)),
    BARK_HELMET(new TCCraftingRecipe(Map.of(TCItems.BARK.get().getInternalName(), 5, TCItems.MUD.get().getInternalName(), 5), TCItems.BARK_HELMET.get().getInternalName(), 1, 1, CraftingRecipesRegistry.RecipeType.ARMOR)),
    BARK_CHESTPLATE(new TCCraftingRecipe(Map.of(TCItems.BARK.get().getInternalName(), 8, TCItems.MUD.get().getInternalName(), 8), TCItems.BARK_CHESTPLATE.get().getInternalName(), 1, 1, CraftingRecipesRegistry.RecipeType.ARMOR)),
    BARK_LEGGINGS(new TCCraftingRecipe(Map.of(TCItems.BARK.get().getInternalName(), 7, TCItems.MUD.get().getInternalName(), 7), TCItems.BARK_LEGGINGS.get().getInternalName(), 1, 1, CraftingRecipesRegistry.RecipeType.ARMOR)),
    BARK_BOOTS(new TCCraftingRecipe(Map.of(TCItems.BARK.get().getInternalName(), 4, TCItems.MUD.get().getInternalName(), 4), TCItems.BARK_BOOTS.get().getInternalName(), 1, 1, CraftingRecipesRegistry.RecipeType.ARMOR)),
    WALKING_STICK(new TCCraftingRecipe(Map.of(TCItems.STICK.get().getInternalName(), 4), TCItems.WALKING_STICK.get().getInternalName(), 1, 2, CraftingRecipesRegistry.RecipeType.TOOL)),

    //石器時代
    STONE_AXE(new TCCraftingRecipe(Map.of(TCItems.WIDESTONE.get().getInternalName(), 1, TCItems.TALLSTONE.get().getInternalName(), 2, TCItems.WOODEN_COMPONENT.get().getInternalName(), 1, TCItems.STICK.get().getInternalName(), 2), TCItems.STONE_AXE.get().getInternalName(), 1, 3, CraftingRecipesRegistry.RecipeType.TOOL)),
    STONE_SHOVEL(new TCCraftingRecipe(Map.of(TCItems.TALLSTONE.get().getInternalName(), 1, TCItems.WOODEN_COMPONENT.get().getInternalName(), 1, TCItems.STICK.get().getInternalName(), 2), TCItems.STONE_SHOVEL.get().getInternalName(), 1, 3, CraftingRecipesRegistry.RecipeType.TOOL)),
    STONE_PICKAXE(new TCCraftingRecipe(Map.of(TCItems.WIDESTONE.get().getInternalName(), 3, TCItems.WOODEN_COMPONENT.get().getInternalName(), 1, TCItems.STICK.get().getInternalName(), 2), TCItems.STONE_PICKAXE.get().getInternalName(), 1, 3, CraftingRecipesRegistry.RecipeType.TOOL)),
    STONE_HOE(new TCCraftingRecipe(Map.of(TCItems.WIDESTONE.get().getInternalName(), 2, TCItems.WOODEN_COMPONENT.get().getInternalName(), 1, TCItems.STICK.get().getInternalName(), 2), TCItems.STONE_HOE.get().getInternalName(), 1, 3, CraftingRecipesRegistry.RecipeType.TOOL)),
    STONE_SWORD(new TCCraftingRecipe(Map.of(TCItems.TALLSTONE.get().getInternalName(), 2, TCItems.WOODEN_COMPONENT.get().getInternalName(), 1, TCItems.STICK.get().getInternalName(), 2), TCItems.STONE_SWORD.get().getInternalName(), 1, 3, CraftingRecipesRegistry.RecipeType.WEAPON)),
    FURNACE(new TCCraftingRecipe(Map.of(TCItems.ROUND_STONE.get().getInternalName(), 6, TCItems.FLINT.get().getInternalName(), 3), TCItems.FURNACE.get().getInternalName(), 1, 10, CraftingRecipesRegistry.RecipeType.MISC)),
    FUEL_BALL(new TCCraftingRecipe(Map.of(TCItems.DRYGRASS.get().getInternalName(), 32), TCItems.FUEL_BALL.get().getInternalName(), 1, 1, CraftingRecipesRegistry.RecipeType.MISC)),
    STONE_HELMET(new TCCraftingRecipe(Map.of(TCItems.TALLSTONE.get().getInternalName(), 10, TCItems.WIDESTONE.get().getInternalName(), 15, TCItems.WOODEN_COMPONENT.get().getInternalName(), 1), TCItems.STONE_HELMET.get().getInternalName(), 1, 3, CraftingRecipesRegistry.RecipeType.ARMOR)),
    STONE_CHESTPLATE(new TCCraftingRecipe(Map.of(TCItems.TALLSTONE.get().getInternalName(), 10, TCItems.WIDESTONE.get().getInternalName(), 30, TCItems.WOODEN_COMPONENT.get().getInternalName(), 1), TCItems.STONE_CHESTPLATE.get().getInternalName(), 1, 3, CraftingRecipesRegistry.RecipeType.ARMOR)),
    STONE_LEGGINGS(new TCCraftingRecipe(Map.of(TCItems.TALLSTONE.get().getInternalName(), 30, TCItems.WIDESTONE.get().getInternalName(), 5, TCItems.WOODEN_COMPONENT.get().getInternalName(), 1), TCItems.STONE_LEGGINGS.get().getInternalName(), 1, 3, CraftingRecipesRegistry.RecipeType.ARMOR)),
    STONE_BOOTS(new TCCraftingRecipe(Map.of(TCItems.TALLSTONE.get().getInternalName(), 40, TCItems.WOODEN_COMPONENT.get().getInternalName(), 1), TCItems.STONE_BOOTS.get().getInternalName(), 1, 3, CraftingRecipesRegistry.RecipeType.ARMOR)),
    STONE_COMPONENT(new TCCraftingRecipe(Map.of(TCItems.ROUND_STONE.get().getInternalName(), 10, TCItems.ANDESITE_STONE.get().getInternalName(), 2, TCItems.GRANITE_STONE.get().getInternalName(), 2, TCItems.DIORITE_STONE.get().getInternalName(), 2), TCItems.STONE_COMPONENT.get().getInternalName(), 1, 10, CraftingRecipesRegistry.RecipeType.MISC)),
    IRON_INGOT(new TCCraftingRecipe(Map.of(TCItems.IRON_SHARD.get().getInternalName(), 3), TCItems.IRON_INGOT.get().getInternalName(), 1, 1, CraftingRecipesRegistry.RecipeType.MISC)),
    GOLD_INGOT(new TCCraftingRecipe(Map.of(TCItems.GOLD_SHARD.get().getInternalName(), 3), TCItems.GOLD_INGOT.get().getInternalName(), 1, 1, CraftingRecipesRegistry.RecipeType.MISC)),

    //鉄器時代
    REINFORCED_STICK(new TCCraftingRecipe(Map.of(TCItems.STICK.get().getInternalName(), 1, TCItems.SAND_DUST.get().getInternalName(), 10, TCItems.GRAVEL_DUST.get().getInternalName(), 10), TCItems.REINFORCED_STICK.get().getInternalName(), 1, 3, CraftingRecipesRegistry.RecipeType.MISC)),
    IRON_AXE(new TCCraftingRecipe(Map.of(TCItems.IRON_INGOT.get().getInternalName(), 3, TCItems.REINFORCED_STICK.get().getInternalName(), 2, TCItems.STONE_COMPONENT.get().getInternalName(), 1), TCItems.IRON_AXE.get().getInternalName(), 1, 5, CraftingRecipesRegistry.RecipeType.TOOL)),
    IRON_SHOVEL(new TCCraftingRecipe(Map.of(TCItems.IRON_INGOT.get().getInternalName(), 1, TCItems.REINFORCED_STICK.get().getInternalName(), 2, TCItems.STONE_COMPONENT.get().getInternalName(), 1), TCItems.IRON_SHOVEL.get().getInternalName(), 1, 5, CraftingRecipesRegistry.RecipeType.TOOL)),
    IRON_PICKAXE(new TCCraftingRecipe(Map.of(TCItems.IRON_INGOT.get().getInternalName(), 3, TCItems.REINFORCED_STICK.get().getInternalName(), 2, TCItems.STONE_COMPONENT.get().getInternalName(), 1), TCItems.IRON_PICKAXE.get().getInternalName(), 1, 5, CraftingRecipesRegistry.RecipeType.TOOL)),
    IRON_HOE(new TCCraftingRecipe(Map.of(TCItems.IRON_INGOT.get().getInternalName(), 2, TCItems.REINFORCED_STICK.get().getInternalName(), 2, TCItems.STONE_COMPONENT.get().getInternalName(), 1), TCItems.IRON_HOE.get().getInternalName(), 1, 5, CraftingRecipesRegistry.RecipeType.TOOL)),
    GOLDEN_AXE(new TCCraftingRecipe(Map.of(TCItems.GOLD_INGOT.get().getInternalName(), 3, TCItems.REINFORCED_STICK.get().getInternalName(), 2, TCItems.STONE_COMPONENT.get().getInternalName(), 1), TCItems.GOLDEN_AXE.get().getInternalName(), 1, 5, CraftingRecipesRegistry.RecipeType.TOOL)),
    GOLDEN_SHOVEL(new TCCraftingRecipe(Map.of(TCItems.GOLD_INGOT.get().getInternalName(), 1, TCItems.REINFORCED_STICK.get().getInternalName(), 2, TCItems.STONE_COMPONENT.get().getInternalName(), 1), TCItems.GOLDEN_SHOVEL.get().getInternalName(), 1, 5, CraftingRecipesRegistry.RecipeType.TOOL)),
    GOLDEN_PICKAXE(new TCCraftingRecipe(Map.of(TCItems.GOLD_INGOT.get().getInternalName(), 3, TCItems.REINFORCED_STICK.get().getInternalName(), 2, TCItems.STONE_COMPONENT.get().getInternalName(), 1), TCItems.GOLDEN_PICKAXE.get().getInternalName(), 1, 5, CraftingRecipesRegistry.RecipeType.TOOL)),
    GOLDEN_HOE(new TCCraftingRecipe(Map.of(TCItems.GOLD_INGOT.get().getInternalName(), 2, TCItems.REINFORCED_STICK.get().getInternalName(), 2, TCItems.STONE_COMPONENT.get().getInternalName(), 1), TCItems.GOLDEN_HOE.get().getInternalName(), 1, 5, CraftingRecipesRegistry.RecipeType.TOOL)),
    REINFORCED_WALKING_STICK(new TCCraftingRecipe(Map.of(TCItems.REINFORCED_STICK.get().getInternalName(), 4), TCItems.REINFORCED_WALKING_STICK.get().getInternalName(), 1, 10, CraftingRecipesRegistry.RecipeType.TOOL));


    @Getter
    private final ITCCraftingRecipe recipe;

    TCCraftingRecipes(ITCCraftingRecipe recipe) {
        this.recipe = recipe;
    }
}