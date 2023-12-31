/*
 * Copyright (c) 2023. RIBLaB
 */
package net.riblab.tradecore.shop;

import lombok.Getter;

import java.util.List;

/**
 * 汎用ショップのデータ
 *
 * @param name         ショップの名前
 * @param shopItemList ショップに並ぶアイテムのリスト
 */
record ShopData(@Getter String name, @Getter List<ShopItem> shopItemList) implements IShopData {

}
