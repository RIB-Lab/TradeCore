package net.riblab.tradecore.shop;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public interface IShopData {
    String getName();

    List<ShopItem> getShopItemList();

    @Data
    @AllArgsConstructor
    class ShopItem {
        ItemStack itemStack;
        double price;
    }
}
