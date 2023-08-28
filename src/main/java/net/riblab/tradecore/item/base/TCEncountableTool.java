package net.riblab.tradecore.item.base;

import lombok.Getter;
import net.kyori.adventure.text.TextComponent;
import net.riblab.tradecore.item.mod.IItemMod;
import net.riblab.tradecore.mob.ITCMob;
import org.bukkit.Material;

import java.util.List;
import java.util.Map;

/**
 * 使用時に確率で敵が沸くツール
 */
public class TCEncountableTool extends TCTool implements ICanSpawnMobOnUse {

    @Getter
    private final Map<ITCMob, Float> spawnTable;

    /**
     * 　固有アイテムの型を作成する
     */
    public TCEncountableTool(TextComponent name, Material material, String internalName, int customModelData, ToolType toolType, int harvestLevel, double miningSpeed, int baseDurability, Map<ITCMob, Float> spawnTable, List<IItemMod> mods) {
        super(name, material, internalName, customModelData, toolType, harvestLevel, miningSpeed, baseDurability, mods);
        this.spawnTable = spawnTable;
    }
}
