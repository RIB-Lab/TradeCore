package net.riblab.tradecore.item;

import lombok.Getter;
import net.riblab.tradecore.item.base.ITCEquipment;
import net.riblab.tradecore.item.base.ITCItem;
import net.riblab.tradecore.item.base.TCItems;
import net.riblab.tradecore.item.mod.IItemMod;
import net.riblab.tradecore.modifier.IModifier;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * プレイヤーが現在装備していたり手に持っていたりするアイテムの持つmodを記録するハンドラ
 */
enum PlayerItemModServiceImpl implements PlayerItemModService {
    INSTANCE;

    /**
     * 全てのプレイヤーとその装備が持つmodのマップ
     */
    private final Map<Player, List<IItemMod<?>>> playerEquipmentModMap = new HashMap<>();

    /**
     * 全てのプレイヤーとそのアイテムが持つmodのマップ
     */
    private final Map<Player, List<IItemMod<?>>> playerMainHandModMap = new HashMap<>();

    /**
     * プレイヤーのアイテムmodが変更された時のイベント
     */
    @Getter
    private final List<Consumer<Player>> onItemModUpdated = new ArrayList<>();

    @Override
    public void updateEquipment(Player player) {
        List<IItemMod<?>> mods = new ArrayList<>();
        for (ItemStack armorContent : player.getInventory().getArmorContents()) {
            ITCItem tcItem = TCItems.toTCItem(armorContent);
            if (!(tcItem instanceof ITCEquipment equipment))
                continue;

            mods.addAll(equipment.getDefaultMods());
        }
        playerEquipmentModMap.put(player, mods);

        onItemModUpdated.forEach(playerConsumer -> playerConsumer.accept(player));
    }

    @Override
    public void updateMainHand(Player player, int newSlot) {
        ITCItem tcItem = TCItems.toTCItem(player.getInventory().getItem(newSlot));
        if (tcItem != null) {
            playerMainHandModMap.put(player, tcItem.getDefaultMods());
        } else {
            playerMainHandModMap.remove(player);
        }

        onItemModUpdated.forEach(playerConsumer -> playerConsumer.accept(player));
    }

    @Override
    public void remove(Player player) {
        playerEquipmentModMap.remove(player);
        playerMainHandModMap.remove(player);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T apply(Player player, T originalValue, Class<? extends IModifier<T>> clazz) {
        //まず装備で修飾
        List<IItemMod<?>> equipmentMods = playerEquipmentModMap.computeIfAbsent(player, k -> new ArrayList<>());

        List<IItemMod<?>> applicableMods = equipmentMods.stream().filter(clazz::isInstance).toList();
        T modifiedValue = originalValue;
        for (IItemMod<?> applicableMod : applicableMods) {
            modifiedValue = ((IModifier<T>) applicableMod).apply(originalValue, modifiedValue);
        }

        //次にメインハンドで修飾
        List<IItemMod<?>> mainhandMods = playerMainHandModMap.computeIfAbsent(player, k -> new ArrayList<>());

        List<IItemMod<?>> applicableMods2 = mainhandMods.stream().filter(clazz::isInstance).toList();
        for (IItemMod<?> applicableMod : applicableMods2) {
            modifiedValue = ((IModifier<T>) applicableMod).apply(originalValue, modifiedValue);
        }

        return modifiedValue;
    }
}
