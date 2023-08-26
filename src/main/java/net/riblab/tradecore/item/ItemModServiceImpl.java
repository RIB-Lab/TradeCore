package net.riblab.tradecore.item;

import lombok.Getter;
import net.riblab.tradecore.item.base.IHasItemMod;
import net.riblab.tradecore.item.base.ITCItem;
import net.riblab.tradecore.item.base.TCEquipment;
import net.riblab.tradecore.item.mod.IItemMod;
import net.riblab.tradecore.modifier.IModifier;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.function.Consumer;

/**
 * プレイヤーが現在装備していたり手に持っていたりするアイテムの持つmodを記録するハンドラ
 */
public class ItemModServiceImpl implements ItemModService {

    /**
     * 全てのプレイヤーとその装備が持つmodのマップ
     */
    private static final Map<Player, List<IItemMod>> playerEquipmentModMap = new HashMap<>();

    /**
     * 全てのプレイヤーとそのアイテムが持つmodのマップ
     */
    private static final Map<Player, List<IItemMod>> playerMainHandModMap = new HashMap<>();

    /**
     * プレイヤーのアイテムmodが変更された時のイベント
     */
    @Getter
    private List<Consumer<Player>> onItemModUpdated = new ArrayList<>();

    @Override
    public void updateEquipment(Player player){
        List<IItemMod> mods = new ArrayList<>();
        for (ItemStack armorContent : player.getInventory().getArmorContents()) {
            ITCItem tcItem = TCItems.toTCItem(armorContent);
            if(!(tcItem instanceof TCEquipment equipment))
                continue;
            
            mods.addAll(equipment.getDefaultMods());
        }
        playerEquipmentModMap.put(player, mods);
        
        onItemModUpdated.forEach(playerConsumer -> playerConsumer.accept(player));
    }

    @Override
    public void updateMainHand(Player player, int newSlot){
        ITCItem tcItem = TCItems.toTCItem(player.getInventory().getItem(newSlot));
        if(tcItem instanceof IHasItemMod modItem){
            playerMainHandModMap.put(player, modItem.getDefaultMods());
        }
        else{
            playerMainHandModMap.remove(player);
        }

        onItemModUpdated.forEach(playerConsumer -> playerConsumer.accept(player));
    }

    @Override
    public void remove(Player player){
        playerEquipmentModMap.remove(player);
        playerMainHandModMap.remove(player);
    }

    @Override
    public <T> T apply(Player player, T originalValue, Class<? extends IModifier<T>> clazz){
        //まず装備で修飾
        List<IItemMod> equipmentMods = playerEquipmentModMap.get(player);
        if(equipmentMods == null){
            equipmentMods = new ArrayList<>();
            playerEquipmentModMap.put(player, equipmentMods);
        }

        List<IItemMod> applicableMods = equipmentMods.stream().filter(clazz::isInstance).toList();
        T modifiedValue = originalValue;
        for (IItemMod applicableMod : applicableMods) {
            modifiedValue = ((IModifier<T>) applicableMod).apply(originalValue, modifiedValue);
        }

        //次にメインハンドで修飾
        List<IItemMod> mainhandMods = playerMainHandModMap.get(player);
        if(mainhandMods == null){
            mainhandMods = new ArrayList<>();
            playerMainHandModMap.put(player, mainhandMods);
        }

        List<IItemMod> applicableMods2 = mainhandMods.stream().filter(clazz::isInstance).toList();
        for (IItemMod applicableMod : applicableMods2) {
            modifiedValue = ((IModifier<T>) applicableMod).apply(originalValue, modifiedValue);
        }

        return modifiedValue;
    }
}
