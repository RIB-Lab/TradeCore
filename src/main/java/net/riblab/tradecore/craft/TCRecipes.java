package net.riblab.tradecore.craft;

import lombok.Getter;
import net.riblab.tradecore.TCResourcePackData;
import net.riblab.tradecore.UICraftingTable;
import net.riblab.tradecore.item.TCItems;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public enum TCRecipes {
    WOODEN_AXE(new TCRecipe(Map.of(TCItems.ROUND_TRUNK.get(), 3, TCItems.STICK.get(), 2), TCItems.WOODEN_AXE.get(), 1, 1, RecipeType.TOOL)),
    WOODEN_SHOVEL(new TCRecipe(Map.of(TCItems.ROUND_TRUNK.get(), 1, TCItems.STICK.get(), 2), TCItems.WOODEN_SHOVEL.get(), 1, 1, RecipeType.TOOL)),
    WOODEN_PICKAXE(new TCRecipe(Map.of(TCItems.ROUND_TRUNK.get(), 3, TCItems.STICK.get(), 2), TCItems.WOODEN_PICKAXE.get(), 1, 1, RecipeType.TOOL)),
    WOODEN_HOE(new TCRecipe(Map.of(TCItems.ROUND_TRUNK.get(), 2, TCItems.STICK.get(), 2), TCItems.WOODEN_HOE.get(), 1, 1, RecipeType.TOOL)),

    STICK(new TCRecipe(Map.of(TCItems.TWIG.get(), 4), TCItems.STICK.get(), 1, 1, RecipeType.MISC));
    
    @Getter
    private final TCRecipe recipe;

    TCRecipes(TCRecipe recipe) {
        this.recipe = recipe;
    }
    
    public static List<TCRecipe> getRecipes(RecipeType type){
        return Arrays.stream(TCRecipes.values()).map(TCRecipes::getRecipe).filter(tcRecipe -> tcRecipe.getCategory() == type).collect(Collectors.toList());
    }

    /**
     * レシピの種類
     */
    public enum RecipeType{
        ARMOR, TOOL, WEAPON, MISC;
    }
}