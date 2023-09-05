package net.riblab.tradecore.craft;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

/**
 * レシピクラス
 */
public final class TCCraftingRecipe implements ITCCraftingRecipe {
    @Getter @Setter
    private Map<String, Integer> ingredients;
    @Getter @Setter
    private String result;
    @Getter @Setter
    private int resultAmount;
    @Getter @Setter
    private double fee;
    @Getter @Setter
    private TCCraftingRecipes.RecipeType category;
    
    public TCCraftingRecipe(){
        
    }

    /**
     * @param ingredients  レシピ素材。同じ種類のアイテムを複数スロットに入れないこと！
     * @param result       レシピ完成品
     * @param resultAmount 完成品の量
     * @param fee          レシピを実行するための費用
     * @param category     レシピのカテゴリ
     */
    public TCCraftingRecipe(Map<String, Integer> ingredients, String result, int resultAmount, double fee,
                            TCCraftingRecipes.RecipeType category) {
        this.ingredients = ingredients;
        this.result = result;
        this.resultAmount = resultAmount;
        this.fee = fee;
        this.category = category;
    }
}
