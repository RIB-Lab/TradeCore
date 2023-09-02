package net.riblab.tradecore.item.base;

import de.tr7zw.nbtapi.NBTCompound;
import de.tr7zw.nbtapi.NBTItem;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.riblab.tradecore.general.NBTTagNames;
import net.riblab.tradecore.item.ItemCreator;
import net.riblab.tradecore.item.mod.IItemMod;
import net.riblab.tradecore.item.mod.ModMaxDurabilityI;
import net.riblab.tradecore.item.mod.ModMiningSpeedI;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

/**
 * カスタム装備のクラス
 */
class TCEquipment extends TCItem implements ITCEquipment {

    @Getter
    private final DurabilityTable durabilityTable;

    /**
     * カスタムのアーマートリムの名前
     */
    private final String trimName;

    @Override
    protected @Nonnull ItemCreator getTemplate() {
        ItemStack itemStack = super.getTemplate()
                .setIntNBT(NBTTagNames.DURABILITY.get(), durabilityTable.getMiddleMaxDurability())
                .setLores(getLore(durabilityTable.getMiddleMaxDurability(), new ArrayList<>())).create();
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
    public TCEquipment(TextComponent name, Material material, String internalName, List<IItemMod<?>> mod, DurabilityTable durabilityTable, String trimName) {
        super(name, material, internalName, 0, mod);
        this.durabilityTable = durabilityTable;
        this.trimName = trimName;
    }
    
    @Override
    public @Nonnull ItemStack getItemStack() {
        int maxDurability = durabilityTable.getRandomMaxDurability();

        List<IItemMod<?>> initMods = List.of(
                new ModMaxDurabilityI(maxDurability));

        return new ItemCreator(getTemplate().create())
                .setIntNBT(NBTTagNames.DURABILITY.get(), maxDurability)
                .setLores(getLore(maxDurability, initMods))
                .writeItemMods(initMods).create();
    }

    @Override
    public List<Component> getLore(int durability, List<IItemMod<?>> randomMods) {
        List<Component> texts = new ArrayList<>();
        if (durabilityTable.getMiddleMaxDurability() != -1) {
            texts.add(getDurabilityLore(durability, randomMods));
        }
        texts.addAll(getDefaultModsLore());
        return texts;
    }
}
