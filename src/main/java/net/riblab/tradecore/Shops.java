package net.riblab.tradecore;

import dev.jorel.commandapi.arguments.Argument;
import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.arguments.CustomArgument;
import dev.jorel.commandapi.arguments.StringArgument;
import lombok.Getter;
import net.riblab.tradecore.item.TCItems;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;

/**
 * ショップのリスト
 */
public enum Shops {
    WEAPON(new ShopData("武器屋(仮置き)", List.of(new ShopData.ShopItem(TCItems.WOODEN_SWORD.get().getItemStack(), 100)))),
    EQUIPMENT(new ShopData("装備屋(仮置き)", List.of(new ShopData.ShopItem(TCItems.BARK_CHESTPLATE.get().getItemStack(), 100)))),
    TOOL(new ShopData("ツール屋(仮置き)", List.of(new ShopData.ShopItem(TCItems.WOODEN_PICKAXE.get().getItemStack(), 100))));
    
    @Getter
    private final ShopData shop;

    Shops(ShopData shop) {
        this.shop = shop;
    }

    /**
     * コマンド文字列をカスタムショップに変換する
     */
    @Nullable
    public static ShopData commandToShop(String command) {
        Shops data = Arrays.stream(Shops.values()).filter(e -> e.toString().equals(command)).findFirst().orElse(null);
        return data == null ? null : data.getShop();
    }

    // Function that returns our custom argument
    public static Argument<ShopData> customShopDataArgument(String nodeName) {

        // Construct our CustomArgument that takes in a String input and returns a World object
        return new CustomArgument<ShopData, String>(new StringArgument(nodeName), info -> {
            // Parse the data from our input
            ShopData data = commandToShop(info.input());

            if (data == null) {
                throw CustomArgument.CustomArgumentException.fromMessageBuilder(new CustomArgument.MessageBuilder("Unknown shop: ").appendArgInput());
            } else {
                return data;
            }
        }).replaceSuggestions(ArgumentSuggestions.strings(info ->
                // List of shops on the server
                Arrays.stream(values()).map(shops -> shops.toString()).toArray(String[]::new))
        );
    }
}
