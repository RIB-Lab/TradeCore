package net.riblab.tradecore.item;

import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;

/**
 * 売却可能な(素材)アイテム
 */
public class TCSellableItem extends TCItem {

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
    protected ItemCreator createItem() {
        return super.createItem().setLore(getLore());
    }

    /**
     *  売却可能なアイテムの説明を生成する
     */
    protected Component getLore() {
        return Component.text("売価：").decoration(TextDecoration.ITALIC, false).color(NamedTextColor.WHITE)
                .append(Component.text((Math.floor(sellPrice * 100)) / 100).color(NamedTextColor.YELLOW));
    }
}
