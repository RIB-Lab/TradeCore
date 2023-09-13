/*
 * Copyright (c) 2023. RIBLaB
 */
package net.riblab.tradecore.loottable;

import lombok.Getter;
import net.riblab.tradecore.item.mod.*;

import java.util.Arrays;
import java.util.Locale;
import java.util.Objects;

/**
 * modの種類と、その短縮形の名前を記録するenum。保存の際に重要
 */
public enum ShortHandLootTableModNames {
    MINHEIGHT(ModMinHeight.class),
    MAXHEIGHT(ModMaxHeight.class),
    ;

    @Getter
    private final Class<? extends ILootTableMod<?>> modClass;

    ShortHandLootTableModNames(Class<? extends ILootTableMod<?>> modClass) {
        this.modClass = modClass;
    }

    /**
     * クラスからそ短縮形の名前(小文字)を取得する
     */
    public static String getShortHandNameFromClass(Class<? extends ILootTableMod<?>> clazz) {
        ShortHandLootTableModNames names = Arrays.stream(ShortHandLootTableModNames.values()).filter(shortHandItemModNames -> shortHandItemModNames.getModClass().equals(clazz)).findFirst().orElse(null);
        return Objects.nonNull(names) ? names.name().toLowerCase(Locale.ROOT) : null;
    }

    /**
     * クラスの短縮形の名前(小文字)からクラスを取得する
     */
    public static Class<? extends ILootTableMod<?>> getClassFromShortHandName(String shortHandName) {
        String capitalName = shortHandName.toUpperCase(Locale.ROOT);
        ShortHandLootTableModNames names = Arrays.stream(ShortHandLootTableModNames.values()).filter(shortHandItemModNames -> shortHandItemModNames.name().equals(capitalName)).findFirst().orElse(null);
        return Objects.nonNull(names) ? names.getModClass() : null;
    }
}
