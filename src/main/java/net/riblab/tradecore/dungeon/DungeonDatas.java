/*
 * Copyright (c) 2023. RIBLaB 
 */
package net.riblab.tradecore.dungeon;

import lombok.Getter;
import net.riblab.tradecore.entity.mob.TCMobs;
import net.riblab.tradecore.item.base.TCItems;
import org.bukkit.util.Vector;

import javax.annotation.Nullable;
import java.util.*;

/**
 * ダンジョンのデータ管理クラス
 * ダンジョンを追加する際必ず dungeons_ + ダンジョン名 のschematicをresourceフォルダに同梱すること！
 */
public enum DungeonDatas {
    TEST(new DungeonData<>(DungeonNames.TEST, new Vector(-17, 97, -24), List.of(TCMobs.DUNGEON_SKELETON.get(), TCMobs.DUNGEON_ZOMBIE.get()),
            3, DPTExtermination.class, 5, Map.of())),
    STONE_ROOM(new DungeonData<>(DungeonNames.STONE_ROOM, new Vector(68.5, 97, -52.5), List.of(TCMobs.DUNGEON_ZOMBIE.get(), TCMobs.DUNGEON_SKELETON.get(), TCMobs.DUNGEON_SILVERFISH.get()),
            3, DPTExtermination.class, 100, Map.of(TCItems.STONE_DAGGER.get(), 0.33f, TCItems.STONE_SPEAR.get(), 0.33f, TCItems.STONE_BATTLEAXE.get(), 0.34f)));

    @Getter
    private final IDungeonData<?> data;

    DungeonDatas(IDungeonData<?> data) {
        this.data = data;
    }

    /**
     * コマンド文字列をダンジョンデータにする
     */
    public static Optional<IDungeonData<?>> commandToDungeonData(@Nullable String command) {
        Optional<DungeonDatas> datas = Arrays.stream(DungeonDatas.values()).filter(e -> e.toString().equals(command)).findFirst();
        return datas.map(DungeonDatas::getData);
    }

    /**
     * ダンジョンの内部名をダンジョンデータにする
     */
    public static Optional<IDungeonData<?>> nameToDungeonData(@Nullable String name) {
        Optional<DungeonDatas> datas = Arrays.stream(DungeonDatas.values()).filter(e -> e.getData().getNames().getInternalName().equals(name)).findFirst();
        return datas.map(DungeonDatas::getData);
    }
}
