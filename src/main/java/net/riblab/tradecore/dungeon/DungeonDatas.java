package net.riblab.tradecore.dungeon;

import dev.jorel.commandapi.arguments.Argument;
import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.arguments.CustomArgument;
import dev.jorel.commandapi.arguments.StringArgument;
import lombok.Getter;
import net.riblab.tradecore.item.base.TCItems;
import net.riblab.tradecore.mob.TCMobs;
import org.bukkit.util.Vector;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * ダンジョンのデータ管理クラス
 * 必ず dungeons_ + ダンジョン名 のschematicをresourceフォルダに同梱すること！
 */
public enum DungeonDatas {
    TEST(new DungeonData<>(DungeonNames.TEST.get(), new Vector(-17, 97, -24), List.of(TCMobs.DUNGEON_SKELETON.get(), TCMobs.DUNGEON_ZOMBIE.get()),
            3, DPTExtermination.class, 5, Map.of())),
    STONEROOM(new DungeonData<>(DungeonNames.STONEROOM.get(), new Vector(68.5, 97, -52.5), List.of(TCMobs.DUNGEON_ZOMBIE.get(), TCMobs.DUNGEON_SKELETON.get(), TCMobs.DUNGEON_SILVERFISH.get()),
            3, DPTExtermination.class, 100, Map.of(TCItems.STONE_DAGGER.get().getItemStack(), 0.33f, TCItems.STONE_SPEAR.get().getItemStack(), 0.33f, TCItems.STONE_BATTLEAXE.get().getItemStack(), 0.34f)));

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
        return datas == null ? null : datas.getData();
    }

    public static Argument<IDungeonData<?>> customDungeonDataArgument(@Nonnull String nodeName) {
        return new CustomArgument<IDungeonData<?>, String>(new StringArgument(nodeName), info -> {
            IDungeonData<?> data = commandToDungeonData(info.input());

            if (data == null) {
                throw CustomArgument.CustomArgumentException.fromMessageBuilder(new CustomArgument.MessageBuilder("Unknown dungeon: ").appendArgInput());
            } else {
                return data;
            }
        }).replaceSuggestions(ArgumentSuggestions.strings(info ->
                Arrays.stream(values()).map(Enum::toString).toArray(String[]::new))
        );
    }

    /**
     * ダンジョン名をダンジョンデータにする
     */
    @Nullable
    public static IDungeonData<?> nameToDungeonData(@Nullable String name) {
        DungeonDatas datas = Arrays.stream(DungeonDatas.values()).filter(e -> e.getData().getName().equals(name)).findFirst().orElse(null);
        return datas == null ? null : datas.getData();
    }
}
