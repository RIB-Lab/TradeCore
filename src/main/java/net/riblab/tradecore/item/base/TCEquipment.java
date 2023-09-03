package net.riblab.tradecore.item.base;

import de.tr7zw.nbtapi.NBTCompound;
import de.tr7zw.nbtapi.NBTItem;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.riblab.tradecore.general.NBTTagNames;
import net.riblab.tradecore.item.ItemCreator;
import net.riblab.tradecore.item.mod.IItemMod;
import net.riblab.tradecore.modifier.IRandomItemModCreator;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

/**
 * カスタム装備のクラス
 */
class TCEquipment extends TCItem {

    /**
     * カスタムのアーマートリムの名前
     */
    private final String trimName;

    @Override
    protected @Nonnull ItemCreator getTemplate() {
        ItemStack itemStack = super.getTemplate().create();
        NBTItem item = new NBTItem(itemStack);
        item.setInteger(NBTTagNames.ARMOR_HIDEFLAGS.get(), 135);//hideattribute + hidearmorupgrade
        NBTCompound nbtList = item.getOrCreateCompound(NBTTagNames.ARMOR_TRIM.get());
        nbtList.setString(NBTTagNames.ARMOR_MATERIAL.get(), "minecraft:" + trimName);
        nbtList.setString(NBTTagNames.ARMOR_PATTERN.get(), "minecraft:" + trimName);
        itemStack = item.getItem();
        return new ItemCreator(itemStack);
    }

    /**
     * 　固有アイテムの型を作成する
     */
    public TCEquipment(TextComponent name, Material material, String internalName, List<IItemMod<?>> mod, String trimName) {
        super(name, material, internalName, 0, mod);
        this.trimName = trimName;
    }
}
