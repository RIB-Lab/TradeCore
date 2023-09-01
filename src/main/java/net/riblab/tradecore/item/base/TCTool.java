package net.riblab.tradecore.item.base;

import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.riblab.tradecore.general.NBTTagNames;
import net.riblab.tradecore.item.ItemCreator;
import net.riblab.tradecore.item.mod.IItemMod;
import net.riblab.tradecore.item.mod.ModMiningSpeedI;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * プラグインのツールクラス
 */
public class TCTool extends TCItem implements ITCTool {

    @Getter
    private final ToolType toolType;

    @Getter
    private final int harvestLevel;

    @Getter
    private final double baseMiningSpeed;

    @Getter
    private final int baseDurability;

    @Getter
    private final List<IItemMod> defaultMods;

    /**
     * 　固有アイテムの型を作成する
     */
    public TCTool(TextComponent name, Material material, String internalName, int customModelData, ToolType toolType, int harvestLevel, double miningSpeed, int baseDurability, List<IItemMod> mods) {
        super(name, material, internalName, customModelData);

        this.toolType = toolType;
        this.harvestLevel = harvestLevel;
        this.baseMiningSpeed = miningSpeed;
        this.baseDurability = baseDurability;
        this.defaultMods = mods;
    }

    @Override
    protected @Nonnull ItemCreator getTemplate() {
        return super.getTemplate().setIntNBT(NBTTagNames.DURABILITY.get(), baseDurability)
                .setLores(getLore(baseDurability, new ArrayList<>()));
    }

    @Override
    public @Nonnull ItemStack getItemStack() {
        //TODO:ちゃんと最初のランダムmodを決めるシステムを作る
        List<IItemMod> initMods = List.of(new ModMiningSpeedI((int)((new Random().nextDouble(mineSpeedRandomness * 2)- mineSpeedRandomness + baseMiningSpeed) * 100)));
        return new ItemCreator(getTemplate().create()).setIntNBT(NBTTagNames.DURABILITY.get(), baseDurability)
                .setLores(getLore(baseDurability, initMods))
                .writeItemMods(initMods).create();
    }

    /**
     * ツールの説明を生成する
     *
     * @param durability インスタンスが持つ耐久値
     * @param randomMods ツールが持つランダムmod
     * @return ツールの説明
     */
    protected List<Component> getLore(int durability, List<IItemMod> randomMods) {
        List<Component> texts = new ArrayList<>();
        if (baseDurability != -1) {
            texts.add(Component.text("耐久値: ").decoration(TextDecoration.ITALIC, false).color(NamedTextColor.WHITE)
                    .append(Component.text(durability).color(durability == baseDurability ? NamedTextColor.WHITE : NamedTextColor.YELLOW))
                    .append(Component.text("/" + baseDurability).color(NamedTextColor.WHITE)));
        }
        for (IItemMod defaultMod : defaultMods) {
            texts.add(Component.text(defaultMod.getLore()).decoration(TextDecoration.ITALIC, false).color(NamedTextColor.WHITE));
        }
        for (IItemMod randomMod : randomMods) {
            texts.add(Component.text(randomMod.getLore()).decoration(TextDecoration.ITALIC, false).color(NamedTextColor.WHITE));
        }
        return texts;
    }

    @Override
    public ItemStack reduceDurability(ItemStack instance, int amount) {
        if (!isSimilar(instance))
            return null;

        int durability = new ItemCreator(instance).getIntNBT(NBTTagNames.DURABILITY.get());

        if (durability == -1) //耐久無限
            return instance;

        durability -= amount;

        if (durability <= 0) //耐久切れ
            return null;

        if (durability > baseDurability) //耐久MAX
            durability = baseDurability;

        int damageToSet = (int) (instance.getType().getMaxDurability() * ((float) durability / (float) baseDurability));
        int damageToDeal = (instance.getType().getMaxDurability() - instance.getDurability()) - damageToSet;
        List<IItemMod> mods = new ItemCreator(instance).getItemMods();
        return new ItemCreator(instance).setLores(getLore(durability, mods)).damage(damageToDeal).setIntNBT(NBTTagNames.DURABILITY.get(), durability).create();
    }
}
