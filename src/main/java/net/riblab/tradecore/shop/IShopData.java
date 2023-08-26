package net.riblab.tradecore.shop;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.bukkit.inventory.ItemStack;

public interface IShopData {
    String getName();

    java.util.List<ShopItem> getShopItemList();

    @Data
    @AllArgsConstructor
    public static class ShopItem {
        ItemStack itemStack;
        double price;
    }
}
