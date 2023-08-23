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
public class EquipmentHandler {
    
    private static final Map<Player, List<ItemMod>> playerModMap = new HashMap<>();

    /**
     * プレイヤーの装備modが変更された時のイベント
     */
    public List<Consumer<Player>> onEquipmentModUpdated = new ArrayList<>();

    /**
     * プレイヤーをスキャンしてmodをリストアップして保存する
     */
    public void update(Player player){
        List<ItemMod> mods = new ArrayList<>();
        for (ItemStack armorContent : player.getInventory().getArmorContents()) {
            ITCItem tcItem = TCItems.toTCItem(armorContent);
            if(!(tcItem instanceof TCEquipment equipment))
                continue;
            
            mods.addAll(equipment.getDefaultMods());
        }
        playerModMap.put(player, mods);
        
        onEquipmentModUpdated.forEach(playerConsumer -> playerConsumer.accept(player));
    }

    /**
     * 保存しているプレイヤーのmodを消去する
     */
    public void remove(Player player){
        playerModMap.remove(player);
    }

    /**
     * ある値をプレイヤーの持つmodで修飾する
     */
    public <T> T apply(Player player, T originalValue, Class<? extends IModifier<T>> clazz){
        UUID uuid = player.getUniqueId();
        List<ItemMod> mods = playerModMap.get(player);
        if(mods == null){
            mods = new ArrayList<>();
            playerModMap.put(player, mods);
        }

        List<ItemMod> applicableMods = mods.stream().filter(clazz::isInstance).toList();
        T modifiedValue = originalValue;
        for (ItemMod applicableMod : applicableMods) {
            modifiedValue = ((IModifier<T>) applicableMod).apply(originalValue, modifiedValue);
        }

        return modifiedValue;
    }
}
