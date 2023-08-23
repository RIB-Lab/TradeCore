package net.riblab.tradecore.ui;

import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import dev.triumphteam.gui.guis.PaginatedGui;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.riblab.tradecore.ShopData;
import net.riblab.tradecore.TradeCore;
import net.riblab.tradecore.item.ItemCreator;
import net.riblab.tradecore.item.TCItems;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

/**
 * カスタムショップのUI
 */
public class UIShop {

    public static PaginatedGui open(Player player, ShopData data) {
        PaginatedGui gui = Gui.paginated()
                .title(Component.text(data.name))
                .rows(6)
                .disableAllInteractions()
                .create();

        GuiItem previousPageButton = new GuiItem(TCItems.PREVIOUS_PAGE.get().getItemStack(),
                event -> gui.previous());
        gui.setItem(48, previousPageButton);
        GuiItem nextPageButton = new GuiItem(TCItems.NEXT_PAGE.get().getItemStack(),
                event -> gui.next());
        gui.setItem(50, nextPageButton);

        for (ShopData.ShopItem shopItem : data.shopItemList) {
            GuiItem blockButton = new GuiItem(new ItemCreator(shopItem.getItemStack().clone()).addLore(Component.text(shopItem.getPrice() + "RIB").decoration(TextDecoration.ITALIC, false).color(NamedTextColor.WHITE)).create(),
                    event -> buy(event, shopItem));
            gui.addItem(blockButton);
        }

        gui.open(player);

        return gui;
    }

    public static void buy(InventoryClickEvent event, ShopData.ShopItem shopItem){
        double balance = TradeCore.getInstance().getEconomy().getBalance((Player)event.getWhoClicked());
        if(balance < shopItem.getPrice()){
            event.getWhoClicked().sendMessage("お金を持っていません！");
            return;
        }

        TradeCore.getInstance().getEconomy().withdrawPlayer((Player)event.getWhoClicked(), shopItem.getPrice());
        event.getWhoClicked().getInventory().addItem(shopItem.getItemStack());
    }
}