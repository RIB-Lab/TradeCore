package net.riblab.tradecore.item;

import net.riblab.tradecore.item.mod.ItemMod;
import net.riblab.tradecore.modifier.IModifier;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.function.Consumer;

/**
 * プレイヤーが現在装備していたり手に持っていたりするアイテムの持つmodを記録するハンドラ
 */
public class ItemModService {

    /**
     * 全てのプレイヤーとその装備が持つmodのマップ
     */
    private static final Map<Player, List<ItemMod>> playerEquipmentModMap = new HashMap<>();

    /**
     * 全てのプレイヤーとそのアイテムが持つmodのマップ
     */
    private static final Map<Player, List<ItemMod>> playerMainHandModMap = new HashMap<>();

    /**
     * プレイヤーのアイテムmodが変更された時のイベント
     */
    public List<Consumer<Player>> onItemModUpdated = new ArrayList<>();

    /**
     * プレイヤーをスキャンして装備modをリストアップして保存する
     */
    public void updateEquipment(Player player){
        List<ItemMod> mods = new ArrayList<>();
        for (ItemStack armorContent : player.getInventory().getArmorContents()) {
            ITCItem tcItem = TCItems.toTCItem(armorContent);
            if(!(tcItem instanceof TCEquipment equipment))
                continue;
            
            mods.addAll(equipment.getDefaultMods());
        }
        playerEquipmentModMap.put(player, mods);
        
        onItemModUpdated.forEach(playerConsumer -> playerConsumer.accept(player));
    }

    /**
     * プレイヤーのメインハンドをスキャンしてアイテムのmodをリストアップして保存する
     * @param newSlot (新しい)メインハンドの場所のスロット番号
     */
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

    /**
     * 保存しているプレイヤーの全てのmodを消去する
     */
    public void remove(Player player){
        playerEquipmentModMap.remove(player);
        playerMainHandModMap.remove(player);
    }

    /**
     * ある値をプレイヤーの持つアイテムmodで修飾する
     */
    public <T> T apply(Player player, T originalValue, Class<? extends IModifier<T>> clazz){
        //まず装備で修飾
        List<ItemMod> equipmentMods = playerEquipmentModMap.get(player);
        if(equipmentMods == null){
            equipmentMods = new ArrayList<>();
            playerEquipmentModMap.put(player, equipmentMods);
        }

        List<ItemMod> applicableMods = equipmentMods.stream().filter(clazz::isInstance).toList();
        T modifiedValue = originalValue;
        for (ItemMod applicableMod : applicableMods) {
            modifiedValue = ((IModifier<T>) applicableMod).apply(originalValue, modifiedValue);
        }

        //次にメインハンドで修飾
        List<ItemMod> mainhandMods = playerMainHandModMap.get(player);
        if(mainhandMods == null){
            mainhandMods = new ArrayList<>();
            playerMainHandModMap.put(player, mainhandMods);
        }

        List<ItemMod> applicableMods2 = mainhandMods.stream().filter(clazz::isInstance).toList();
        for (ItemMod applicableMod : applicableMods2) {
            modifiedValue = ((IModifier<T>) applicableMod).apply(originalValue, modifiedValue);
        }

        return modifiedValue;
    }
}
