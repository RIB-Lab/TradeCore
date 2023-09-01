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

/**
 * ショップのリスト
 */
public enum Shops {
    WEAPON(new ShopData("武器屋", List.of(new ShopData.ShopItem(TCItems.WOODEN_SWORD.get().getItemStack(), 100),
            new ShopData.ShopItem(TCItems.STONE_SPEAR.get().getItemStack(), 10),
            new ShopData.ShopItem(TCItems.STONE_DAGGER.get().getItemStack(), 10),
            new ShopData.ShopItem(TCItems.STONE_BATTLEAXE.get().getItemStack(), 10)
    ))),
    EQUIPMENT(new ShopData("装備屋", List.of(new ShopData.ShopItem(TCItems.EMERALD_HELMET.get().getItemStack(), 100),
            new ShopData.ShopItem(TCItems.EMERALD_CHESTPLATE.get().getItemStack(), 100),
            new ShopData.ShopItem(TCItems.EMERALD_LEGGINGS.get().getItemStack(), 100),
            new ShopData.ShopItem(TCItems.EMERALD_BOOTS.get().getItemStack(), 100),
            new ShopData.ShopItem(TCItems.WORKER_HELMET.get().getItemStack(), 30),
            new ShopData.ShopItem(TCItems.WORKER_CHESTPLATE.get().getItemStack(), 30),
            new ShopData.ShopItem(TCItems.WORKER_LEGGINGS.get().getItemStack(), 30),
            new ShopData.ShopItem(TCItems.WORKER_BOOTS.get().getItemStack(), 30),
            new ShopData.ShopItem(TCItems.WATER_HELMET.get().getItemStack(), 30)
    ))),

    TOOL(new ShopData("ツール屋", List.of(new ShopData.ShopItem(TCItems.DESTRUCTORS_WAND.get().getItemStack(), 1),
            new ShopData.ShopItem(TCItems.BASIC_BOW.get().getItemStack(), 1000)
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
        return data == null ? null : data.getShop();
    }

    // Function that returns our custom argument
    public static Argument<IShopData> customShopDataArgument(String nodeName) {

        // Construct our CustomArgument that takes in a String input and returns a World object
        return new CustomArgument<>(new StringArgument(nodeName), info -> {
            // Parse the data from our input
            IShopData data = commandToShop(info.input());

            if (data == null) {
                throw CustomArgument.CustomArgumentException.fromMessageBuilder(new CustomArgument.MessageBuilder("Unknown shop: ").appendArgInput());
            } else {
                return data;
            }
        }).replaceSuggestions(ArgumentSuggestions.strings(info ->
                // List of shops on the server
                Arrays.stream(values()).map(Enum::toString).toArray(String[]::new))
        );
    }
}
