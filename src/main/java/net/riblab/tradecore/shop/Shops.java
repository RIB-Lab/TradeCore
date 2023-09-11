/*
 * Copyright (c) 2023. RIBLaB
 */
package net.riblab.tradecore.shop;

import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * ショップのリスト
 */
public enum Shops {
    WEAPON(new ShopData("武器屋", List.of(new ShopData.ShopItem("wooden_sword", 100),
            new ShopData.ShopItem("stone_spear", 10),
            new ShopData.ShopItem("stone_dagger", 10),
            new ShopData.ShopItem("stone_battleaxe", 10)
    ))),
    EQUIPMENT(new ShopData("装備屋", List.of(new ShopData.ShopItem("emerald_helmtet", 100),
            new ShopData.ShopItem("emerald_chestplate", 100),
            new ShopData.ShopItem("emerald_leggings", 100),
            new ShopData.ShopItem("emerald_boots", 100),
            new ShopData.ShopItem("worker_helmet", 30),
            new ShopData.ShopItem("worker_chestplate", 30),
            new ShopData.ShopItem("worker_leggings", 30),
            new ShopData.ShopItem("worker_boots", 30),
            new ShopData.ShopItem("water_helmet", 30)
    ))),

    TOOL(new ShopData("ツール屋", List.of(new ShopData.ShopItem("destructors_wand", 1),
            new ShopData.ShopItem("basic_bow", 1000)
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
