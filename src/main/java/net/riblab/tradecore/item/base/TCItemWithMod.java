package net.riblab.tradecore.item.base;

import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.riblab.tradecore.item.ItemCreator;
import net.riblab.tradecore.item.mod.IItemMod;
import org.bukkit.Material;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

class TCItemWithMod extends TCItem implements IHasItemMod {

    @Getter
    private final List<IItemMod> defaultMods;
    
    /**
     * 　固有アイテムの型を作成する
     */
    public TCItemWithMod(TextComponent name, Material material, String internalName, int customModelData, List<IItemMod> defaultMods) {
        super(name, material, internalName, customModelData);
        this.defaultMods = defaultMods;
    }
    
    @Override
    protected @Nonnull ItemCreator createItem() {
        return super.createItem().setLores(getLore());
    }
    

    protected List<Component> getLore() {
        List<Component> texts = new ArrayList<>();
        for (IItemMod defaultMod : getDefaultMods()) {
            texts.add(Component.text(defaultMod.getLore()).decoration(TextDecoration.ITALIC, false).color(NamedTextColor.WHITE));
        }
        return texts;
    }
}
