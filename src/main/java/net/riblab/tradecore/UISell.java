package net.riblab.tradecore;

import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.PaginatedGui;
import net.kyori.adventure.text.Component;
import net.riblab.tradecore.item.ITCItem;
import net.riblab.tradecore.item.TCItems;
import net.riblab.tradecore.item.TCSellableItem;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;

public class UISell {

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
