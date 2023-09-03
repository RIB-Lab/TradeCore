package net.riblab.tradecore.item.base;

import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.riblab.tradecore.item.ItemCreator;
import net.riblab.tradecore.item.mod.IItemMod;
import net.riblab.tradecore.item.mod.ModRandomAttackDamageI;
import net.riblab.tradecore.modifier.IRandomItemModCreator;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class TCWeapon extends TCItem implements ITCWeapon {

    @Getter
    private final IWeaponAttribute attribute;

    /**
     * 　固有アイテムの型を作成する
     */
    public TCWeapon(TextComponent name, Material material, String internalName, int customModelData, List<IItemMod<?>> defaultMods, IWeaponAttribute attribute) {
        super(name, material, internalName, customModelData, defaultMods);
        this.attribute = attribute;
    }


    @Override
    protected @Nonnull ItemCreator getTemplate() {
        return super.getTemplate().setAttackSpeedAttr(attribute.getAttackSpeed());
    }


    /**
     * 武器の説明を生成する
     *
     * @return ツールの説明
     */
    public List<Component> getLore(List<IItemMod<?>> randomMods) {
        List<Component> texts = new ArrayList<>();
        texts.add(Component.text("攻撃速度: " + Math.floor(attribute.getAttackSpeedForDisplay() * 100) / 100).decoration(TextDecoration.ITALIC, false).color(NamedTextColor.WHITE));
        texts.addAll(getDefaultModsLore());
        texts.addAll(getRandomModsLore(randomMods));
        return texts;
    }
}
