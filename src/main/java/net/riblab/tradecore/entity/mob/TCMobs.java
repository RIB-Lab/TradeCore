/*
 * Copyright (c) 2023. RIBLaB
 */
package net.riblab.tradecore.entity.mob;

import de.tr7zw.nbtapi.NBTEntity;
import net.kyori.adventure.text.Component;
import net.riblab.tradecore.general.NBTTagNames;
import net.riblab.tradecore.item.base.TCItems;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Mob;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * カスタムモブの定義一覧
 */
public enum TCMobs {
    //ダンジョン専用
    DUNGEON_ZOMBIE(new TCMob(EntityType.ZOMBIE, Component.text("リリースまでにテクスチャの実装が間に合わなかった何か"), 10, "dungeon_zombie", Map.of())),
    DUNGEON_SKELETON(new TCMob(EntityType.SKELETON, Component.text("リリースまでに肉の実装が間に合わなかった何か"), 8, "dungeon_skeleton", Map.of())),
    DUNGEON_SILVERFISH(new TCMob(EntityType.SILVERFISH, Component.text("テクスチャもクソもない何か"), 5, "dungeon_silverfish", Map.of())),

    //フィールド専用
    BASIC_SILVERFISH(new TCMob(EntityType.SILVERFISH, Component.text("ふぃっしゅ数ver1"), 12, "basic_silverfish", Map.of("round_stone", 0.25f, "map_stoneroom", 0.25f))),
    BASIC_TREANT(new Treant());

    private final ITCMob ITCMob;

    TCMobs(ITCMob ITCMob) {
        this.ITCMob = ITCMob;
    }

    public ITCMob get() {
        return ITCMob;
    }

    /**
     * モブをカスタムモブに変換する
     */
    public static Optional<ITCMob> toTCMob(@Nullable Mob mob) {
        if (Objects.isNull(mob))
            return Optional.empty();

        NBTEntity nbtEntity = new NBTEntity(mob);
        String ID = nbtEntity.getPersistentDataContainer().getString(NBTTagNames.MOBID.get());

        return Arrays.stream(TCMobs.values()).filter(e -> e.get().getInternalName().equals(ID)).findFirst().map(TCMobs::get);
    }

    /**
     * コマンド文字列をカスタムモブに変換する
     */
    @ParametersAreNonnullByDefault
    public static Optional<ITCMob> commandToTCMob(String command) {
        return Arrays.stream(TCMobs.values()).filter(e -> e.get().getInternalName().equals(command)).findFirst().map(TCMobs::get);
    }
}