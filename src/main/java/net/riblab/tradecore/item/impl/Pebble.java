package net.riblab.tradecore.item.impl;

import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.riblab.tradecore.item.ItemCreator;
import net.riblab.tradecore.item.base.DurabilityTable;
import net.riblab.tradecore.item.base.ISellable;
import net.riblab.tradecore.item.base.MiningSpeedTable;
import net.riblab.tradecore.item.base.TCTool;
import net.riblab.tradecore.item.mod.IItemMod;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class Pebble extends TCTool implements ISellable {

    @Getter
    private final double sellPrice;

    /**
     * 　固有アイテムの型を作成する
     */
    public Pebble(TextComponent name, Material material, String internalName, int customModelData, ToolType toolType, int harvestLevel, MiningSpeedTable miningSpeedTable, DurabilityTable baseDurability, List<IItemMod> mods, double sellPrice) {
        super(name, material, internalName, customModelData, toolType, harvestLevel, miningSpeedTable, baseDurability, mods);
        this.sellPrice = sellPrice;
    }

    @Override
    protected @Nonnull ItemCreator getTemplate() {
        return super.getTemplate().setLores(getLore());
    }

    @Override
    public @Nonnull ItemStack getItemStack() {
        return getTemplateItemStack();
    }

    /**
     * 売却可能なアイテムの説明を生成する
     */
    protected List<Component> getLore() {//小石にランダムmodはない
        List<Component> text = new ArrayList<>();
        text.add(Component.text("売価：").decoration(TextDecoration.ITALIC, false).color(NamedTextColor.WHITE)
                .append(Component.text((Math.floor(sellPrice * 100)) / 100).color(NamedTextColor.YELLOW)));
        return text;
    }
}
