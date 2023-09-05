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

/**
 * カスタムモブの定義一覧
 */
public enum TCMobs {
    //ダンジョン専用
    DUNGEON_ZOMBIE(new TCMob(EntityType.ZOMBIE, Component.text("リリースまでにテクスチャの実装が間に合わなかった何か"), 10, "dungeon_zombie", Map.of())),
    DUNGEON_SKELETON(new TCMob(EntityType.SKELETON, Component.text("リリースまでに肉の実装が間に合わなかった何か"), 8, "dungeon_skeleton", Map.of())),
    DUNGEON_SILVERFISH(new TCMob(EntityType.SILVERFISH, Component.text("テクスチャもクソもない何か"), 5, "dungeon_silverfish", Map.of())),
    
    //フィールド専用
    BASIC_SILVERFISH(new TCMob(EntityType.SILVERFISH, Component.text("ふぃっしゅ数ver1"), 12, "basic_silverfish", Map.of(TCItems.ROUND_STONE.get(), 0.25f, TCItems.MAP_STONEROOM.get(), 0.25f))),
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
    @Nullable
    public static ITCMob toTCMob(@Nullable Mob mob) {
        if (Objects.isNull(mob))
            return null;

        NBTEntity nbtEntity = new NBTEntity(mob);
        String ID = nbtEntity.getPersistentDataContainer().getString(NBTTagNames.MOBID.get());

        TCMobs itcMob = Arrays.stream(TCMobs.values()).filter(e -> e.get().getInternalName().equals(ID)).findFirst().orElse(null);
        return Objects.isNull(itcMob) ? null : itcMob.get();
    }

    /**
     * コマンド文字列をカスタムモブに変換する
     */
    @Nullable
    @ParametersAreNonnullByDefault
    public static ITCMob commandToTCMob(String command) {
        TCMobs itcMob = Arrays.stream(TCMobs.values()).filter(e -> e.get().getInternalName().equals(command)).findFirst().orElse(null);
        return Objects.isNull(itcMob) ? null : itcMob.get();
    }
}