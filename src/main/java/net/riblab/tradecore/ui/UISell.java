package net.riblab.tradecore.ui;

import dev.triumphteam.gui.guis.BaseGui;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.PaginatedGui;
import net.kyori.adventure.text.Component;
import net.riblab.tradecore.integration.TCEconomy;
import net.riblab.tradecore.item.base.IHasItemMod;
import net.riblab.tradecore.item.base.ITCItem;
import net.riblab.tradecore.item.base.TCItems;
import net.riblab.tradecore.entity.mob.FakeVillagerService;
import net.riblab.tradecore.item.mod.ModSellPriceI;
import net.riblab.tradecore.modifier.ISellPriceModifier;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;

/**
 * 売却画面
 */
final class UISell implements IUI{

    /**
     * 売却画面を開く
     */
    @Override
    public BaseGui open(Player player) {
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
        int totalAmount = 0;

        for (ItemStack content : event.getInventory().getContents()) {
            if (content == null)
                continue;

            ITCItem itcItem = TCItems.toTCItem(content);
            if (!(itcItem instanceof IHasItemMod iHasItemMod)) {
                event.getPlayer().getWorld().dropItemNaturally(event.getPlayer().getLocation(), content);
                continue;
            }
            
            ISellPriceModifier mod = (ISellPriceModifier) iHasItemMod.getDefaultMods().stream().filter(iItemMod -> iItemMod instanceof ISellPriceModifier).findFirst().orElse(null);
            if(mod == null){
                event.getPlayer().getWorld().dropItemNaturally(event.getPlayer().getLocation(), content);
                continue;
            }

            totalGain += mod.apply(0d, 0d) * content.getAmount();
            totalAmount += content.getAmount();
        }

        if (totalGain != 0) {
            TCEconomy.getImpl().depositPlayer((Player) event.getPlayer(), totalGain);
            event.getPlayer().sendMessage(Component.text(totalAmount + "個のアイテムを売って、" + Math.floor(totalGain * 100) / 100 + "RIB入手しました"));
        }

        FakeVillagerService.getImpl().tryDeSpawnFakeVillager((Player) event.getPlayer());
    }
}
