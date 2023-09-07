/*
 * Copyright (c) 2023. RIBLaB
 */
package net.riblab.tradecore.craft;

import lombok.Getter;
import net.riblab.tradecore.item.base.TCItems;

import java.util.Map;

/**
 * 精錬レシピレジストリ
 */
public enum TCFurnaceRecipes {
    TORCH(new TCFurnaceRecipe(Map.of(TCItems.STICK.get(), 1), TCItems.TORCH.get(), 1, 1)),
    ASH(new TCFurnaceRecipe(Map.of(TCItems.ROUND_TRUNK.get(), 1), TCItems.ASH.get(), 1, 1)),
    IRON_SHARD(new TCFurnaceRecipe(Map.of(TCItems.METEORIC_IRON_ORE.get(), 1, TCItems.ASH.get(), 1), TCItems.IRON_SHARD.get(), 1, 3)),
    GOLD_SHARD(new TCFurnaceRecipe(Map.of(TCItems.SANDGOLD.get(), 1, TCItems.ASH.get(), 1), TCItems.GOLD_SHARD.get(), 1, 3));

    @Getter
    private final ITCFurnaceRecipe recipe;

    TCFurnaceRecipes(ITCFurnaceRecipe recipe) {
        this.recipe = recipe;
    }
}
