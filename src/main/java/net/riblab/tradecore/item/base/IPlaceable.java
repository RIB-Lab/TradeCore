package net.riblab.tradecore.item.base;

import net.kyori.adventure.text.TextComponent;
import org.bukkit.Material;

/**
 * 設置可能なアイテムタグ
 */
public interface IPlaceable {
}

class TCPlacableItem extends TCItem implements IPlaceable {

    /**
     * 　固有アイテムの型を作成する
     */
    public TCPlacableItem(TextComponent name, Material material, String internalName, int customModelData) {
        super(name, material, internalName, customModelData);
    }
}
