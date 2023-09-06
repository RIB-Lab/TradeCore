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
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * yamlで書かれたアイテムをデシリアライズしたものを保持するクラス
 */
public enum TCItemRegistry {
    INSTANCE;

    /**
     * デシリアライズしたアイテム
     */
    @Getter
    private static final List<ITCItem> deserializedItems = new ArrayList<>();//TODO:ゲッターを削除して読み書きをメソッド経由で行うように

    /**
     * アイテムが固有アイテムであった場合その実体を固有アイテムクラスに変換する<br>
     * この際実体特有のNBTなどは失われる
     *
     * @param itemStack 変換したいアイテム
     * @return 変換された固有アイテム
     */
    @Nullable
    public static ITCItem toTCItem(ItemStack itemStack) {
        if (Objects.isNull(itemStack) || itemStack.getType() == Material.AIR)
            return null;

        String id = new ItemCreator(itemStack).getStrNBT(NBTTagNames.ITEMID.get());
        ITCItem itcItem = deserializedItems.stream().filter(e -> e.isSimilar(id)).findFirst().orElse(null);
        return Objects.isNull(itcItem) ? null : itcItem;
    }

    /**
     * 固有アイテムのinternalNameを固有アイテムに変換する
     *
     * @param internalName 内部名称
     * @return 変換された固有アイテム
     */
    @Nullable
    public static ITCItem commandToTCItem(String internalName) {
        ITCItem itcItem = deserializedItems.stream().filter(e -> e.isSimilar(internalName)).findFirst().orElse(null);
        return Objects.isNull(itcItem) ? null : itcItem;
    }
}
