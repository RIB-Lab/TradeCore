/*
 * Copyright (c) 2023. RIBLaB 
 */
package net.riblab.tradecore.item.base;

import lombok.Getter;
import net.riblab.tradecore.general.NBTTagNames;
import net.riblab.tradecore.item.ItemCreator;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nullable;
import java.util.*;

/**
 * yamlで書かれたアイテムをデシリアライズしたものを保持するクラス
 */
public enum TCItemRegistry {
    INSTANCE;

    /**
     * デシリアライズしたアイテム
     */
    private final List<ITCItem> deserializedItems = new ArrayList<>();//TODO:ゲッターを削除して読み書きをメソッド経由で行うように

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
        return id.flatMap(s -> deserializedItems.stream().filter(e -> e.isSimilar(s)).findFirst());
    }

    /**
     * 固有アイテムのinternalNameを固有アイテムに変換する
     *
     * @param internalName 内部名称
     * @return 変換された固有アイテム
     */
    public Optional<ITCItem> commandToTCItem(String internalName) {
        return deserializedItems.stream().filter(e -> e.isSimilar(internalName)).findFirst();
    }
    
    public void clear(){
        deserializedItems.clear();
    }
    
    public void addAll(List<ITCItem> items){
        deserializedItems.addAll(items);
    }

    /**
     * 変更負荷なアイテムレジストリのコピーを渡す
     * @return
     */
    public Collection<ITCItem> getItems(){
        return List.copyOf(deserializedItems);
    }
}
