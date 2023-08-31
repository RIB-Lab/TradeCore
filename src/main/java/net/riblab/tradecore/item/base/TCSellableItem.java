package net.riblab.tradecore.item.base;

import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.riblab.tradecore.item.ItemCreator;
import org.bukkit.Material;

import javax.annotation.Nonnull;

/**
 * 売却可能な(素材)アイテム
 */
class TCSellableItem extends TCItem implements ISellable {

    @Getter
    private final double sellPrice;

    /**
     * 　固有アイテムの型を作成する
     */
    public TCSellableItem(TextComponent name, Material material, String internalName, int customModelData, double sellPrice) {
        super(name, material, internalName, customModelData);
        this.sellPrice = sellPrice;
    }

    @Override
    protected @Nonnull ItemCreator createItem() {
        return super.createItem().setLore(getLore());
    }

    /**
     * 売却可能なアイテムの説明を生成する
     */
    protected Component getLore() {
        return Component.text("売価：").decoration(TextDecoration.ITALIC, false).color(NamedTextColor.WHITE)
                .append(Component.text((Math.floor(sellPrice * 100)) / 100).color(NamedTextColor.YELLOW));
    }
}
