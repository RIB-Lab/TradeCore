package net.riblab.tradecore.shop;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

/**
 * 汎用ショップのデータ
 *
 * @param name         ショップの名前
 * @param shopItemList ショップに並ぶアイテムのリスト
 */
record ShopData(@Getter String name, @Getter List<ShopItem> shopItemList) implements IShopData {

}
