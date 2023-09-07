/*
 * Copyright (c) 2023. RIBLaB 
 */
package net.riblab.tradecore.ui;

import dev.triumphteam.gui.components.GuiType;
import dev.triumphteam.gui.guis.BaseGui;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import net.kyori.adventure.text.Component;
import net.riblab.tradecore.advancement.Advancements;
import net.riblab.tradecore.dungeon.DungeonDatas;
import net.riblab.tradecore.dungeon.DungeonService;
import net.riblab.tradecore.dungeon.IDungeonData;
import net.riblab.tradecore.general.ErrorMessages;
import net.riblab.tradecore.item.base.ITCItem;
import net.riblab.tradecore.item.base.TCItems;
import net.riblab.tradecore.modifier.IEnterDungeonModifier;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;
import java.util.Optional;

/**
 * ダンジョン進入UI
 */
final class UIDungeonEnter implements IUI {

    @Override
    public BaseGui open(Player player) {
        if(!Advancements.STONE_AXE.get().isGranted(player)){
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
        Optional<ITCItem> iTCItem = TCItems.toTCItem(map);
        if(iTCItem.isEmpty())
            return;

        IEnterDungeonModifier mod = (IEnterDungeonModifier) iTCItem.get().getDefaultMods().stream().filter(iItemMod -> iItemMod instanceof IEnterDungeonModifier).findFirst().orElse(null);
        if(Objects.nonNull(mod)){
            DungeonService IDungeonService = DungeonService.getImpl();
            IDungeonData<?> data = DungeonDatas.nameToDungeonData(mod.apply(null, null).getInternalName()).orElseThrow(()-> new NullPointerException(ErrorMessages.INVAILD_DUNGEON_NAME.get()));
            World instance = IDungeonService.create(data, -1).orElseThrow(()-> new NullPointerException(ErrorMessages.FAILED_TO_GENERATE_DUNGEON_WORLD.get()));
            IDungeonService.enter((Player) event.getPlayer(), instance);
        }
    }
}
