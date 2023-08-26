package net.riblab.tradecore.item.base;

import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.riblab.tradecore.item.ItemCreator;
import net.riblab.tradecore.item.mod.IItemMod;
import org.bukkit.Material;

import java.util.List;

public class Pebble extends TCTool implements ISellable {

    @Getter
    private final double sellPrice;
    
    /**
     * 　固有アイテムの型を作成する
     */
    public Pebble(TextComponent name, Material material, String internalName, int customModelData, ToolType toolType, int harvestLevel, double miningSpeed, int baseDurability, List<IItemMod> mods, double sellPrice) {
        super(name, material, internalName, customModelData, toolType, harvestLevel, miningSpeed, baseDurability, mods);
        this.sellPrice = sellPrice;
    }

    @Override
    protected ItemCreator createItem() {
        return super.createItem().setLores(getLore());
    }

    /**
     *  売却可能なアイテムの説明を生成する
     */
    protected List<Component> getLore() {
        List<Component> text = super.getLore(-1);
        text.add(Component.text("売価：").decoration(TextDecoration.ITALIC, false).color(NamedTextColor.WHITE)
                .append(Component.text((Math.floor(sellPrice * 100)) / 100).color(NamedTextColor.YELLOW)));
        return text;
    }
}
