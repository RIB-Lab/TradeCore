package net.riblab.tradecore.ui;

import dev.triumphteam.gui.components.GuiType;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import dev.triumphteam.gui.guis.PaginatedGui;
import net.kyori.adventure.text.Component;
import net.riblab.tradecore.dungeon.DungeonDatas;
import net.riblab.tradecore.dungeon.DungeonService;
import net.riblab.tradecore.dungeon.IDungeonData;
import net.riblab.tradecore.item.base.ITCDungeonMap;
import net.riblab.tradecore.item.base.ITCItem;
import net.riblab.tradecore.item.base.TCItems;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;

/**
 * ダンジョン進入UI
 */
public final class UIDungeonEnter {
    public static Gui open(Player player) {
        Gui gui = Gui.gui(GuiType.CHEST)
                .title(Component.text("マップを置いてください"))
                .rows(1)
                .enableAllInteractions()
                .create();

        GuiItem disableInteractionButton = new GuiItem(new ItemStack(Material.RED_STAINED_GLASS_PANE), event -> event.setCancelled(true));
        gui.getFiller().fill(disableInteractionButton);
        
        GuiItem mapSlot = new GuiItem(new ItemStack(Material.AIR));
        gui.setItem(4, mapSlot);

        gui.setCloseGuiAction(UIDungeonEnter::onClose);

        gui.open(player);

        return gui;
    }

    public static void onClose(InventoryCloseEvent event) {
        ItemStack map = event.getInventory().getContents()[4];
        ITCItem tcMap = TCItems.toTCItem(map);
        if(tcMap instanceof ITCDungeonMap dungeonMap){
            IDungeonData<?> data = DungeonDatas.nameToDungeonData(dungeonMap.getDungeonName().get());
            DungeonService IDungeonService = DungeonService.getImpl();
            World instance = IDungeonService.create(data, -1);
            IDungeonService.enter((Player) event.getPlayer(), instance);
        }
    }
}
