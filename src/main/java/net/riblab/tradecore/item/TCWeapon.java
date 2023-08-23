package net.riblab.tradecore.item;

import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.riblab.tradecore.item.mod.ItemMod;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class TCWeapon extends TCItem implements ITCWeapon{

    @Getter
    private final int baseDurability;

    @Getter
    private final List<ItemMod> defaultMods;

    /**
     * 　固有アイテムの型を作成する
     */
    public TCWeapon(TextComponent name, Material material, String internalName, int customModelData, int baseDurability, List<ItemMod> defaultMods) {
        super(name, material, internalName, customModelData);
        this.baseDurability = baseDurability;
        this.defaultMods = defaultMods;
    }

    @Override
    protected ItemCreator createItem() {
        return super.createItem().setIntNBT(durabilityTag, baseDurability)
                .setLores(getLore(baseDurability));
    }

    /**
     * 武器の説明を生成する
     *
     * @param durability インスタンスが持つ耐久値
     * @return ツールの説明
     */
    protected List<Component> getLore(int durability) {
        List<Component> texts = new ArrayList<>();
        if (baseDurability != -1) {
            texts.add(Component.text("耐久値: ").decoration(TextDecoration.ITALIC, false).color(NamedTextColor.WHITE)
                    .append(Component.text(durability).color(durability == baseDurability ? NamedTextColor.WHITE : NamedTextColor.YELLOW))
                    .append(Component.text("/" + baseDurability).color(NamedTextColor.WHITE)));
        }
        for (ItemMod defaultMod : defaultMods) {
            texts.add(Component.text(defaultMod.getLore()).decoration(TextDecoration.ITALIC, false).color(NamedTextColor.WHITE));
        }
        return texts;
    }
    
    @Override
    public ItemStack reduceDurability(ItemStack instance) {
        if (!isSimilar(instance))
            return null;

        Integer nbt = new ItemCreator(instance).getIntNBT(durabilityTag);
        int durability = nbt;

        if (durability == -1) //耐久無限
            return instance;

        durability--;
        if (durability <= 0) //耐久切れ
            return null;

        int damageToSet = (int) (instance.getType().getMaxDurability() * ((float) durability / (float) baseDurability));
        int damageToDeal = (instance.getType().getMaxDurability() - instance.getDurability()) - damageToSet;
        return new ItemCreator(instance).setLores(getLore(durability)).damage(damageToDeal).setIntNBT(durabilityTag, durability).create();
    }
}
