package net.riblab.tradecore.item;

import lombok.Getter;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.Material;

public class TCSellableItem extends TCItem{
    
    @Getter
    private final double sellPrice;
    
    /**
     * 　固有アイテムの型を作成する
     */
    public TCSellableItem(TextComponent name, Material material, String internalName, int customModelData, double sellPrice) {
        super(name, material, internalName, customModelData);
        this.sellPrice = sellPrice;
    }
}
