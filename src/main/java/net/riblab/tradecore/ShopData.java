package net.riblab.tradecore;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

/**
 * 汎用ショップのデータ
 */
@RequiredArgsConstructor
public class ShopData implements IShopData {

    /**
     * ショップの名前
     */
    @Getter
    private final String name;
    /**
     * ショップに並ぶアイテムのリスト
     */
    @Getter
    private final List<ShopItem> shopItemList;

}
