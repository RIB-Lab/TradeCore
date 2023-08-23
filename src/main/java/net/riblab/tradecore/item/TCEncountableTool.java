package net.riblab.tradecore.item;

import lombok.Getter;
import net.kyori.adventure.text.TextComponent;
import net.riblab.tradecore.item.mod.ItemMod;
import net.riblab.tradecore.mob.TCMob;
import org.bukkit.Material;

import java.util.List;
import java.util.Map;

/**
 * 使用時に確率で敵が沸くツール
 */
public class TCEncountableTool extends TCTool {

    /**
     * 沸く敵の種類と確率(0~1)のテーブル
     */
    @Getter
    private final Map<TCMob, Float> spawnTable;

    /**
     * 　固有アイテムの型を作成する
     */
    public TCEncountableTool(TextComponent name, Material material, String internalName, int customModelData, ToolType toolType, int harvestLevel, double miningSpeed, int baseDurability, Map<TCMob, Float> spawnTable, List<ItemMod> mods) {
        super(name, material, internalName, customModelData, toolType, harvestLevel, miningSpeed, baseDurability, mods);
        this.spawnTable = spawnTable;
    }
}
