package net.riblab.tradecore.ui;

import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.PaginatedGui;
import net.kyori.adventure.text.Component;
import net.riblab.tradecore.FakeVillagerService;
import net.riblab.tradecore.TradeCore;
import net.riblab.tradecore.item.ITCItem;
import net.riblab.tradecore.item.TCItems;
import net.riblab.tradecore.item.TCSellableItem;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;

/**
 * 売却画面
 */
public class UISell {

    /**
     * 売却画面を開く
     */
    public static PaginatedGui open(Player player) {
        PaginatedGui gui = Gui.paginated()
                .title(Component.text("売却"))
                .rows(3)
                .enableAllInteractions()
                .create();

        gui.setCloseGuiAction(UISell::onClose);

        gui.open(player);

        return gui;
    }

    /**
     * 売却画面が閉じられた時、中身の売却(ドロップ)処理を行う
     */
    public static void onClose(InventoryCloseEvent event) {
        if (event.getReason() == InventoryCloseEvent.Reason.OPEN_NEW)
            return;

        double totalGain = 0;

        for (ItemStack content : event.getInventory().getContents()) {
            if (content == null)
                continue;

            ITCItem itcItem = TCItems.toTCItem(content);
            if (!(itcItem instanceof TCSellableItem)) {
                event.getPlayer().getWorld().dropItemNaturally(event.getPlayer().getLocation(), content);
                continue;
            }

            totalGain += ((TCSellableItem) itcItem).getSellPrice() * content.getAmount();
        }

        if (totalGain != 0)
            TradeCore.getInstance().getEconomy().depositPlayer((Player) event.getPlayer(), totalGain);

        FakeVillagerService.tryDeSpawnFakeVillager((Player) event.getPlayer());
    }
}