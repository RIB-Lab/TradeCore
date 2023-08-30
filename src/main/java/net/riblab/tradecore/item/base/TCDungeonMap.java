package net.riblab.tradecore.item.base;

import lombok.Getter;
import net.kyori.adventure.text.TextComponent;
import net.riblab.tradecore.dungeon.DungeonNames;
import org.bukkit.Material;

/**
 * ダンジョン入場券
 */
class TCDungeonMap extends TCItem implements ITCDungeonMap {

    /**
     * このマップを使うことで入れるダンジョン
     */
    @Getter
    private final DungeonNames dungeonName;

    /**
     * 　固有アイテムの型を作成する
     */
    public TCDungeonMap(TextComponent name, Material material, String internalName, int customModelData, DungeonNames dungeonName) {
        super(name, material, internalName, customModelData);
        this.dungeonName = dungeonName;
    }
}
