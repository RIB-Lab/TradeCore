package net.riblab.tradecore.ui;

import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import dev.triumphteam.gui.guis.PaginatedGui;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.riblab.tradecore.ItemCreator;
import net.riblab.tradecore.TradeCore;
import net.riblab.tradecore.item.TCItems;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

/**
 * 臨時アドミンショップのUI。使い捨て
 */
public class UIAdminShop {

    private static final float exchangeRate = 0.5f;
    private static final float foodPrice = 0.25f;
    
    /**
     * ショップ画面を開く
     */
    public static PaginatedGui open(Player player) {
        PaginatedGui gui = Gui.paginated()
                .title(Component.text("臨時ショップ"))
                .rows(1)
                .disableAllInteractions()
                .create();

        GuiItem playTicketButton = new GuiItem(new ItemCreator(TCItems.COIN.get().getItemStack()).setName(Component.text("プレイチケット1枚を" + exchangeRate + "RIBに変換する").decoration(TextDecoration.ITALIC, false)).create(),
                UIAdminShop::exchange);
        gui.setItem(3, playTicketButton);
        GuiItem foodButton = new GuiItem(new ItemCreator(TCItems.MESI.get().getItemStack()).setName(Component.text("臨時食料を買う").decoration(TextDecoration.ITALIC, false))
                .setLore(Component.text("料理システムが実装され次第削除されます！").decoration(TextDecoration.ITALIC, false).color(NamedTextColor.RED)).create(),
                UIAdminShop::buyFood);
        gui.setItem(4, foodButton);

        gui.open(player);

        return gui;
    }

    /**
     * プレイチケットを現金にする
     */
    public static void exchange(InventoryClickEvent event){
        int playTickets = TradeCore.getInstance().getEconomy().getPlayTickets((Player)event.getWhoClicked());
        if(playTickets <= 0){
            event.getWhoClicked().sendMessage("チケットを持っていません！");
            return;
        }

        TradeCore.getInstance().getEconomy().withdrawTickets((Player)event.getWhoClicked(), 1);
        TradeCore.getInstance().getEconomy().depositPlayer((Player)event.getWhoClicked(), exchangeRate);
    }

    /**
     * 食料を買う
     */
    public static void buyFood(InventoryClickEvent event){
        double balance = TradeCore.getInstance().getEconomy().getBalance((Player)event.getWhoClicked());
        if(balance <= foodPrice){
            event.getWhoClicked().sendMessage("お金を持っていません！");
            return;
        }
        
        TradeCore.getInstance().getEconomy().withdrawPlayer((Player)event.getWhoClicked(), foodPrice);
        event.getWhoClicked().getInventory().addItem(TCItems.MESI.get().getItemStack());
    }
}
