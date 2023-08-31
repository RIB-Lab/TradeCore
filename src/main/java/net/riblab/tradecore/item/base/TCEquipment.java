package net.riblab.tradecore.item.base;

import de.tr7zw.nbtapi.NBTCompound;
import de.tr7zw.nbtapi.NBTItem;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.riblab.tradecore.item.ItemCreator;
import net.riblab.tradecore.item.mod.IItemMod;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

/**
 * カスタム装備のクラス
 */
class TCEquipment extends TCItem implements ITCEquipment {

    /**
     * 装備が既定で持つmodのリスト
     */
    @Getter
    private final List<IItemMod> defaultMods;

    /**
     * 装備の基礎耐久値。-1で無限
     */
    @Getter
    private final int baseDurability;

    /**
     * カスタムのアーマートリムの名前
     */
    private final String trimName;

    private static final String durabilityTag = "durability";

    @Override
    protected @Nonnull ItemCreator createItem() {
        ItemStack itemStack = super.createItem()
                .setIntNBT(durabilityTag, baseDurability)
                .setLores(getLore(baseDurability)).create();
        NBTItem item = new NBTItem(itemStack);
        item.setInteger("HideFlags", 135);//hideattribute + hidearmorupgrade
        NBTCompound nbtList = item.getOrCreateCompound("Trim");
        nbtList.setString("material", "minecraft:" + trimName);
        nbtList.setString("pattern", "minecraft:" + trimName);
        itemStack = item.getItem();
        return new ItemCreator(itemStack);
    }

    /**
     * 　固有アイテムの型を作成する
     */
    public TCEquipment(TextComponent name, Material material, String internalName, List<IItemMod> mod, int baseDurability, String trimName) {
        super(name, material, internalName, 0);
        this.defaultMods = mod;
        this.baseDurability = baseDurability;
        this.trimName = trimName;
    }

    @Override
    public List<Component> getLore(int durability) {
        List<Component> texts = new ArrayList<>();
        if (baseDurability != -1) {
            texts.add(Component.text("耐久値: ").decoration(TextDecoration.ITALIC, false).color(NamedTextColor.WHITE)
                    .append(Component.text(durability).color(durability == baseDurability ? NamedTextColor.WHITE : NamedTextColor.YELLOW))
                    .append(Component.text("/" + baseDurability).color(NamedTextColor.WHITE)));
        }
        for (IItemMod defaultMod : defaultMods) {
            texts.add(Component.text(defaultMod.getLore()).decoration(TextDecoration.ITALIC, false).color(NamedTextColor.WHITE));
        }
        return texts;
    }

    @Override
    public ItemStack reduceDurability(ItemStack instance) {
        if (!isSimilar(instance))
            return null;

        int durability = new ItemCreator(instance).getIntNBT(durabilityTag);

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
