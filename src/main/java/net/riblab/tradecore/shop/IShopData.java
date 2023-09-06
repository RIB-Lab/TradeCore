/*
 * Copyright (c) 2023. RIBLaB 
 */
package net.riblab.tradecore.shop;

import lombok.AllArgsConstructor;
import lombok.Data;
import net.riblab.tradecore.item.base.ITCItem;

import java.util.List;

public interface IShopData {
    String name();

    List<ShopItem> shopItemList();

    @Data
    @AllArgsConstructor
    class ShopItem {
        ITCItem itemToSell;
        double price;
    }
}
