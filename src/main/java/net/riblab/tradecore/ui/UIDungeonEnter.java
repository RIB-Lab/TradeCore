package net.riblab.tradecore.ui;

import dev.triumphteam.gui.components.GuiType;
import dev.triumphteam.gui.guis.BaseGui;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import net.kyori.adventure.text.Component;
import net.riblab.tradecore.dungeon.DungeonDatas;
import net.riblab.tradecore.dungeon.DungeonService;
import net.riblab.tradecore.dungeon.IDungeonData;
import net.riblab.tradecore.advancement.Advancements;
import net.riblab.tradecore.item.base.ITCItem;
import net.riblab.tradecore.item.base.TCItems;
import net.riblab.tradecore.modifier.IEnterDungeonModifier;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;

/**
 * ダンジョン進入UI
 */
final class UIDungeonEnter implements IUI {

    @Override
    public BaseGui open(Player player) {
        if(!Advancements.STONEAXE.get().isGranted(player)){
            player.sendMessage("もっと強くなってからくるんだな (先に石の斧の進捗を開放しましょう)");
            return null;
        }
        
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
        ITCItem tcItem = TCItems.toTCItem(map);
        if(tcItem == null)
            return;

        IEnterDungeonModifier mod = (IEnterDungeonModifier) tcItem.getDefaultMods().stream().filter(iItemMod -> iItemMod instanceof IEnterDungeonModifier).findFirst().orElse(null);
        if(mod != null){
            IDungeonData<?> data = DungeonDatas.nameToDungeonData(mod.apply(null, null).getInternalName());
            DungeonService IDungeonService = DungeonService.getImpl();
            World instance = IDungeonService.create(data, -1);
            IDungeonService.enter((Player) event.getPlayer(), instance);
        }
    }
}
