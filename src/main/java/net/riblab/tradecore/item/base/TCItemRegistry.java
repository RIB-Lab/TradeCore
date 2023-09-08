/*
 * Copyright (c) 2023. RIBLaB
 */
package net.riblab.tradecore.item.base;

import net.riblab.tradecore.general.NBTTagNames;
import net.riblab.tradecore.item.ItemCreator;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.*;

/**
 * yamlで書かれたアイテムをデシリアライズしたものを保持するクラス
 */
public enum TCItemRegistry {
    INSTANCE;

    /**
     * デシリアライズしたアイテム
     */
    private final Map<String, ITCItem> deserializedItems = new HashMap<>();

    /**
     * アイテムが固有アイテムであった場合その実体を固有アイテムクラスに変換する<br>
     * この際実体特有のNBTなどは失われる
     *
     * @param itemStack 変換したいアイテム
     * @return 変換された固有アイテム
     */
    public Optional<ITCItem> toTCItem(ItemStack itemStack) {
        if (Objects.isNull(itemStack) || itemStack.getType() == Material.AIR)
            return Optional.empty();

        Optional<String> id = new ItemCreator(itemStack).getStrNBT(NBTTagNames.ITEMID.get());
        return id.map(deserializedItems::get);
    }

    /**
     * 固有アイテムのinternalNameを固有アイテムに変換する
     *
     * @param internalName 内部名称
     * @return 変換された固有アイテム
     */
    public Optional<ITCItem> commandToTCItem(String internalName) {
        return Optional.ofNullable(deserializedItems.get(internalName));
    }

    public void clear() {
        deserializedItems.clear();
    }

    public void addAll(Map<String, ITCItem> items) {
        deserializedItems.putAll(items);
    }

    /**
     * 変更不可なアイテムレジストリのコピーを渡す
     *
     * @return
     */
    public Map<String, ITCItem> getItems() {
        return Collections.unmodifiableMap(deserializedItems);
    }
}
