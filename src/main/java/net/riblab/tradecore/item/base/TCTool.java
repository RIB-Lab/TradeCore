package net.riblab.tradecore.item.base;

import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.riblab.tradecore.item.ItemCreator;
import net.riblab.tradecore.item.mod.IItemMod;
import net.riblab.tradecore.item.mod.ModRandomMiningSpeedI;
import net.riblab.tradecore.modifier.IMiningSpeedModifier;
import net.riblab.tradecore.modifier.IRandomItemModCreator;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

/**
 * プラグインのツールクラス
 */
public class TCTool extends TCItem implements ITCTool {

    @Getter
    private final ToolType toolType;

    @Getter
    private final int harvestLevel;

    /**
     * 　固有アイテムの型を作成する
     */
    public TCTool(TextComponent name, Material material, String internalName, int customModelData, ToolType toolType, int harvestLevel, List<IItemMod<?>> defaultMods) {
        super(name, material, internalName, customModelData, defaultMods);

        this.toolType = toolType;
        this.harvestLevel = harvestLevel;
    }
}
