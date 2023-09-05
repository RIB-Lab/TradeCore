package net.riblab.tradecore.dungeon;

import lombok.Getter;
import net.riblab.tradecore.entity.mob.TCMobs;
import net.riblab.tradecore.item.base.TCItems;
import org.bukkit.util.Vector;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * ダンジョンのデータ管理クラス
 * ダンジョンを追加する際必ず dungeons_ + ダンジョン名 のschematicをresourceフォルダに同梱すること！
 */
public enum DungeonDatas {
    TEST(new DungeonData<>(DungeonNames.TEST, new Vector(-17, 97, -24), List.of(TCMobs.DUNGEON_SKELETON.get(), TCMobs.DUNGEON_ZOMBIE.get()),
            3, DPTExtermination.class, 5, Map.of())),
    STONEROOM(new DungeonData<>(DungeonNames.STONEROOM, new Vector(68.5, 97, -52.5), List.of(TCMobs.DUNGEON_ZOMBIE.get(), TCMobs.DUNGEON_SKELETON.get(), TCMobs.DUNGEON_SILVERFISH.get()),
            3, DPTExtermination.class, 100, Map.of(TCItems.STONE_DAGGER.get(), 0.33f, TCItems.STONE_SPEAR.get(), 0.33f, TCItems.STONE_BATTLEAXE.get(), 0.34f)));

    @Getter
    private final IDungeonData<?> data;

    DungeonDatas(IDungeonData<?> data) {
        this.data = data;
    }

    /**
     * コマンド文字列をダンジョンデータにする
     */
    @Nullable
    public static IDungeonData<?> commandToDungeonData(@Nullable String command) {
        DungeonDatas datas = Arrays.stream(DungeonDatas.values()).filter(e -> e.toString().equals(command)).findFirst().orElse(null);
        return Objects.isNull(datas) ? null : datas.getData();
    }

    /**
     * ダンジョンの内部名をダンジョンデータにする
     */
    @Nullable
    public static IDungeonData<?> nameToDungeonData(@Nullable String name) {
        DungeonDatas datas = Arrays.stream(DungeonDatas.values()).filter(e -> e.getData().getNames().getInternalName().equals(name)).findFirst().orElse(null);
        return Objects.isNull(datas) ? null : datas.getData();
    }
}
