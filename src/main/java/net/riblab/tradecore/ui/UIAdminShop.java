package net.riblab.tradecore.ui;

import dev.triumphteam.gui.guis.BaseGui;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import dev.triumphteam.gui.guis.PaginatedGui;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.riblab.tradecore.integration.TCEconomy;
import net.riblab.tradecore.item.ItemCreator;
import net.riblab.tradecore.item.Materials;
import net.riblab.tradecore.item.base.TCItems;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

/**
 * 臨時アドミンショップのUI。使い捨て
 */
final class UIAdminShop implements IUI {

    private static final float exchangeRate = 0.5f;
    private static final float foodPrice = 0.25f;

    /**
     * ショップ画面を開く
     */
    @Override
    public BaseGui open(Player player) {
        PaginatedGui gui = Gui.paginated()
                .title(Component.text("臨時ショップ"))
                .rows(6)
                .disableAllInteractions()
                .create();

        GuiItem playTicketButton = new GuiItem(new ItemCreator(TCItems.COIN.get().getTemplateItemStack()).setName(Component.text("プレイチケット1枚を" + exchangeRate + "RIBに変換する").decoration(TextDecoration.ITALIC, false)).create(),
                UIAdminShop::exchange);
        gui.addItem(playTicketButton);
        GuiItem foodButton = new GuiItem(new ItemCreator(TCItems.MESI.get().getTemplateItemStack()).setName(Component.text("臨時食料を買う").decoration(TextDecoration.ITALIC, false))
                .setLore(Component.text("料理システムが実装され次第削除されます！").decoration(TextDecoration.ITALIC, false).color(NamedTextColor.RED)).create(),
                UIAdminShop::buyFood);
        gui.addItem(foodButton);

        GuiItem previousPageButton = new GuiItem(TCItems.PREVIOUS_PAGE.get().getTemplateItemStack(),
                event -> gui.previous());
        gui.setItem(48, previousPageButton);
        GuiItem nextPageButton = new GuiItem(TCItems.NEXT_PAGE.get().getTemplateItemStack(),
                event -> gui.next());
        gui.setItem(50, nextPageButton);

        for (Material value : Material.values()) {
            if (!value.isBlock())
                continue;

            if (Materials.UNBREAKABLE.get().contains(value))
                continue;

            if (Materials.BANNED_FROM_SHOP.get().contains(value))
                continue;

            GuiItem blockButton = new GuiItem(new ItemCreator(value).setLore(Component.text("1RIB").decoration(TextDecoration.ITALIC, false)).create(),
                    event -> buyBlock(event, value));
            gui.addItem(blockButton);
        }

        gui.open(player);

        return gui;
    }

    /**
     * プレイチケットを現金にする
     */
    public static void exchange(InventoryClickEvent event) {
        int playTickets = TCEconomy.getImpl().getPlayTickets((Player) event.getWhoClicked());
        if (playTickets <= 0) {
            event.getWhoClicked().sendMessage("チケットを持っていません！");
            return;
        }

        TCEconomy.getImpl().withdrawTickets((Player) event.getWhoClicked(), 1);
        TCEconomy.getImpl().depositPlayer((Player) event.getWhoClicked(), exchangeRate);
    }

    /**
     * 食料を買う
     */
    public static void buyFood(InventoryClickEvent event) {
        double balance = TCEconomy.getImpl().getBalance((Player) event.getWhoClicked());
        if (balance <= foodPrice) {
            event.getWhoClicked().sendMessage("お金を持っていません！");
            return;
        }

        TCEconomy.getImpl().withdrawPlayer((Player) event.getWhoClicked(), foodPrice);
        event.getWhoClicked().getInventory().addItem(TCItems.MESI.get().getTemplateItemStack());
    }

    public static void buyBlock(InventoryClickEvent event, Material material) {
        double balance = TCEconomy.getImpl().getBalance((Player) event.getWhoClicked());
        if (balance < 1) {
            event.getWhoClicked().sendMessage("お金を持っていません！");
            return;
        }

        TCEconomy.getImpl().withdrawPlayer((Player) event.getWhoClicked(), 1);
        event.getWhoClicked().getInventory().addItem(new ItemStack(material));
    }
}
