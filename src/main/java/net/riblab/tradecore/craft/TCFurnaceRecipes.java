/*
 * Copyright (c) 2023. RIBLaB
 */
package net.riblab.tradecore.craft;

import lombok.Getter;

import java.util.Map;

/**
 * 精錬レシピレジストリ TODO:かまどが正式に実装されたらconfigに全部移す
 */
public enum TCFurnaceRecipes {
    TORCH(new TCFurnaceRecipe(Map.of("stick", 1), "vanilla_torch", 1, 1)),
    ASH(new TCFurnaceRecipe(Map.of("round_trunk", 1), "ash", 1, 1)),
    IRON_SHARD(new TCFurnaceRecipe(Map.of("meteoric_iron_ore", 1, "ash", 1), "iron_shard", 1, 3)),
    GOLD_SHARD(new TCFurnaceRecipe(Map.of("sandgold", 1, "ash", 1), "gold_shard", 1, 3));

    @Getter
    private final ITCFurnaceRecipe recipe;

    TCFurnaceRecipes(ITCFurnaceRecipe recipe) {
        this.recipe = recipe;
    }
}
