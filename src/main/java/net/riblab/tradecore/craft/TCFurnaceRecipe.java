/*
 * Copyright (c) 2023. RIBLaB 
 */
package net.riblab.tradecore.craft;

import net.riblab.tradecore.item.base.ITCItem;

import java.util.Map;

/**
 * 精錬レシピクラス
 *
 * @param ingredients  レシピ素材。同じ種類のアイテムを複数スロットに入れないこと！
 * @param result       レシピ完成品
 * @param resultAmount 完成品の量
 * @param fuelAmount   レシピを実行するために消費する燃料の量
 */
record TCFurnaceRecipe(Map<ITCItem, Integer> ingredients, ITCItem result, int resultAmount,
                       int fuelAmount) implements ITCFurnaceRecipe {
}
