package net.riblab.tradecore.item;

import de.tr7zw.nbtapi.NBTCompound;
import de.tr7zw.nbtapi.NBTItem;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.riblab.tradecore.ItemCreator;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

/**
 * カスタム装備のクラス
 */
public class TCEquipment extends TCItem {

    /**
     * 装備の持つアーマー
     */
    @Getter
    private final int armor;

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
    protected ItemCreator createItem() {
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
    public TCEquipment(TextComponent name, Material material, String internalName, int armor, int baseDurability, String trimName) {
        super(name, material, internalName, 0);
        this.armor = armor;
        this.baseDurability = baseDurability;
        this.trimName = trimName;
    }

    /**
     * 装備の説明を生成する
     *
     * @param durability インスタンスが持つ耐久値
     * @return 装備の説明
     */
    protected List<Component> getLore(int durability) {
        List<Component> texts = new ArrayList<>();
        if (baseDurability != -1) {
            texts.add(Component.text("耐久値: ").decoration(TextDecoration.ITALIC, false).color(NamedTextColor.WHITE)
                    .append(Component.text(durability).color(durability == baseDurability ? NamedTextColor.WHITE : NamedTextColor.YELLOW))
                    .append(Component.text("/" + baseDurability).color(NamedTextColor.WHITE)));
        }
        texts.add(Component.text("アーマー: ").decoration(TextDecoration.ITALIC, false).color(NamedTextColor.WHITE)
                .append(Component.text((Math.floor(armor * 10)) / 10)));
        return texts;
    }

    /**
     * 装備のインスタンスの耐久値を1減らす
     */
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
