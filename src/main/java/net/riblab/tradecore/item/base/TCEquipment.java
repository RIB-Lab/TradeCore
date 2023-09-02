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
class TCEquipment extends TCItem implements ITCEquipment {

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
    
    @Override
    public @Nonnull ItemStack getItemStack() {
        List<IRandomItemModCreator> mods = getDefaultMods().stream().filter(iItemMod -> iItemMod instanceof IRandomItemModCreator).map(iItemMod -> (IRandomItemModCreator) iItemMod).toList();
        List<IItemMod<?>> randomMods = new ArrayList<>();
        for (IRandomItemModCreator mod : mods) {
            randomMods = mod.apply(randomMods, randomMods);
        }

        List<IItemMod<?>> initMods = new ArrayList<>();
        initMods.addAll(randomMods);

        return new ItemCreator(getTemplate().create())
                .setLores(getLore(initMods))
                .writeItemMods(initMods).create();
    }

    @Override
    public List<Component> getLore(List<IItemMod<?>> randomMods) {
        List<Component> texts = new ArrayList<>();
        texts.addAll(getDefaultModsLore());
        texts.addAll(getRandomModsLore(randomMods));
        return texts;
    }

    /**
     * ツールに付与されているランダムmodの説明文を取得する
     */
    private List<TextComponent> getRandomModsLore(List<IItemMod<?>> randomMods){
        List<TextComponent> texts = new ArrayList<>();

        for (IItemMod<?> randomMod : randomMods) {
            if(randomMod.getLore() != null)
                texts.add(Component.text(randomMod.getLore()).decoration(TextDecoration.ITALIC, false).color(NamedTextColor.WHITE));
        }

        return texts;
    }
}
