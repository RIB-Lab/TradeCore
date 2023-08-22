package net.riblab.tradecore;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.bukkit.inventory.ItemStack;

import java.util.List;

/**
 * 汎用ショップのデータ
 */
@RequiredArgsConstructor
public class ShopData {

    /**
     * ショップの名前
     */
    public final String name;
    /**
     * ショップに並ぶアイテムのリスト
     */
    public final List<ShopItem> shopItemList;

    @Data
    @AllArgsConstructor
    public static class ShopItem{
        ItemStack itemStack;
        double price;
    }
}
