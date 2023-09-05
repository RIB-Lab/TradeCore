package net.riblab.tradecore.shop;

import dev.jorel.commandapi.arguments.Argument;
import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.arguments.CustomArgument;
import dev.jorel.commandapi.arguments.StringArgument;
import lombok.Getter;
import net.riblab.tradecore.item.base.TCItems;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

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
    @Nullable
    public static IShopData commandToShop(String command) {
        Shops data = Arrays.stream(Shops.values()).filter(e -> e.toString().equals(command)).findFirst().orElse(null);
        return Objects.isNull(data) ? null : data.getShop();
    }
}
