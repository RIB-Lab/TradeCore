/*
 * Copyright (c) 2023. RIBLaB 
 */
package net.riblab.tradecore.shop;

import lombok.Getter;
import net.riblab.tradecore.item.base.TCItems;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * ショップのリスト
 */
public enum Shops {
    WEAPON(new ShopData("武器屋", List.of(new ShopData.ShopItem(TCItems.WOODEN_SWORD.get(), 100),
            new ShopData.ShopItem(TCItems.STONE_SPEAR.get(), 10),
            new ShopData.ShopItem(TCItems.STONE_DAGGER.get(), 10),
            new ShopData.ShopItem(TCItems.STONE_BATTLEAXE.get(), 10)
    ))),
    EQUIPMENT(new ShopData("装備屋", List.of(new ShopData.ShopItem(TCItems.EMERALD_HELMET.get(), 100),
            new ShopData.ShopItem(TCItems.EMERALD_CHESTPLATE.get(), 100),
            new ShopData.ShopItem(TCItems.EMERALD_LEGGINGS.get(), 100),
            new ShopData.ShopItem(TCItems.EMERALD_BOOTS.get(), 100),
            new ShopData.ShopItem(TCItems.WORKER_HELMET.get(), 30),
            new ShopData.ShopItem(TCItems.WORKER_CHESTPLATE.get(), 30),
            new ShopData.ShopItem(TCItems.WORKER_LEGGINGS.get(), 30),
            new ShopData.ShopItem(TCItems.WORKER_BOOTS.get(), 30),
            new ShopData.ShopItem(TCItems.WATER_HELMET.get(), 30)
    ))),

    TOOL(new ShopData("ツール屋", List.of(new ShopData.ShopItem(TCItems.DESTRUCTORS_WAND.get(), 1),
            new ShopData.ShopItem(TCItems.BASIC_BOW.get(), 1000)
    )));

    @Getter
    private final IShopData shop;

    Shops(IShopData shop) {
        this.shop = shop;
    }

    /**
     * コマンド文字列をカスタムショップに変換する
     */
    public static Optional<IShopData> commandToShop(String command) {
        return Arrays.stream(Shops.values()).filter(e -> e.toString().equals(command)).findFirst().map(Shops::getShop);
    }
}
