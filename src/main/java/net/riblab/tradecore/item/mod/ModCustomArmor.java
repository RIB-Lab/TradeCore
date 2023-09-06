/*
 * Copyright (c) 2023. RIBLaB 
 */
package net.riblab.tradecore.item.mod;

import de.tr7zw.nbtapi.NBTCompound;
import de.tr7zw.nbtapi.NBTItem;
import net.riblab.tradecore.general.NBTTagNames;
import net.riblab.tradecore.modifier.IItemTemplateModifier;
import org.bukkit.inventory.ItemStack;

import java.util.Optional;

/**
 * カスタムアーマーを定義する
 */
public class ModCustomArmor extends ItemMod<String> implements IItemTemplateModifier {
    public ModCustomArmor(String param) {
        super(param);
    }

    @Override
    public Optional<String> getLore() {
        return Optional.empty();
    }

    @Override
    public ItemStack apply(ItemStack originalValue, ItemStack modifiedValue) {
        NBTItem item = new NBTItem(modifiedValue);
        item.setInteger(NBTTagNames.ARMOR_HIDEFLAGS.get(), 135);//hideattribute + hidearmorupgrade
        NBTCompound nbtList = item.getOrCreateCompound(NBTTagNames.ARMOR_TRIM.get());
        nbtList.setString(NBTTagNames.ARMOR_MATERIAL.get(), "minecraft:" + getParam());
        nbtList.setString(NBTTagNames.ARMOR_PATTERN.get(), "minecraft:" + getParam());
        modifiedValue = item.getItem();
        return modifiedValue;
    }
}
