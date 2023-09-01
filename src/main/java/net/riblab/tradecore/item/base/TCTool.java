package net.riblab.tradecore.item.base;

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
import net.riblab.tradecore.modifier.IDurabilityModifier;
import net.riblab.tradecore.modifier.IMiningSpeedModifier;
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
    private final DurabilityTable durabilityTable;

    @Getter
    private final List<IItemMod> defaultMods;

    /**
     * 　固有アイテムの型を作成する
     */
    public TCTool(TextComponent name, Material material, String internalName, int customModelData, ToolType toolType, int harvestLevel, double miningSpeed, DurabilityTable durabilityTable, List<IItemMod> mods) {
        super(name, material, internalName, customModelData);

        this.toolType = toolType;
        this.harvestLevel = harvestLevel;
        this.baseMiningSpeed = miningSpeed;
        this.durabilityTable = durabilityTable;
        this.defaultMods = mods;
    }

    @Override
    protected @Nonnull ItemCreator getTemplate() {
        return super.getTemplate().setIntNBT(NBTTagNames.DURABILITY.get(), durabilityTable.getMiddleMaxDurability())
                .setLores(getLore(durabilityTable.getMiddleMaxDurability(), new ArrayList<>()));
    }

    @Override
    public @Nonnull ItemStack getItemStack() {
        //TODO:ちゃんと最初のランダムmodを決めるシステムを作る
        int miningSpeed = (int)((new Random().nextDouble(mineSpeedRandomness * 2)- mineSpeedRandomness + baseMiningSpeed) * 100);
        int maxDurability = new Random().nextInt(durabilityTable.getMinMaxDurability(), durabilityTable.getMaxMaxDurability() + 1);
        List<IItemMod> initMods = List.of(
                new ModMiningSpeedI(miningSpeed),
                new ModMaxDurabilityI(maxDurability));
        return new ItemCreator(getTemplate().create())
                .setIntNBT(NBTTagNames.DURABILITY.get(), maxDurability)
                .setLores(getLore(maxDurability, initMods))
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
        if (durabilityTable.getMiddleMaxDurability() != -1) {
            IItemMod maxDurabilityMod = randomMods.stream().filter(iItemMod -> iItemMod instanceof IDurabilityModifier).findFirst().orElse(null);
            int maxDurability = maxDurabilityMod != null ? maxDurabilityMod.getLevel() : durabilityTable.getMiddleMaxDurability();
            
            texts.add(Component.text("耐久値: ").decoration(TextDecoration.ITALIC, false).color(NamedTextColor.WHITE)
                    .append(Component.text(durability).color(durability == maxDurability ? NamedTextColor.WHITE : NamedTextColor.YELLOW))
                    .append(Component.text("/" + maxDurability).color(NamedTextColor.WHITE)));
        }
        
        for (IItemMod defaultMod : defaultMods) {
            texts.add(Component.text(defaultMod.getLore()).decoration(TextDecoration.ITALIC, false).color(NamedTextColor.WHITE));
        }
        for (IItemMod randomMod : randomMods) {
            if(randomMod instanceof IDurabilityModifier){//上で追加した
                continue;    
            }
            
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

        List<IItemMod> mods = new ItemCreator(instance).getItemMods();
        IItemMod maxDurabilityMod = mods.stream().filter(iItemMod -> iItemMod instanceof IDurabilityModifier).findFirst().orElse(null);
        int maxDurability = maxDurabilityMod != null ? maxDurabilityMod.getLevel() : durabilityTable.getMiddleMaxDurability(); //アイテムにランダムな最大耐久値が付与されていなかったらフォールバックとして基礎最大耐久値を使う
        
        if (durability > maxDurability) //耐久MAX
            durability = maxDurability;

        int damageToSet = (int) (instance.getType().getMaxDurability() * ((float) durability / (float) maxDurability));
        int damageToDeal = (instance.getType().getMaxDurability() - instance.getDurability()) - damageToSet;
        return new ItemCreator(instance).setLores(getLore(durability, new ItemCreator(instance).getItemMods())).damage(damageToDeal).setIntNBT(NBTTagNames.DURABILITY.get(), durability).create();
    }
    
    @Override
    public double getActualMiningSpeed(ItemStack itemStack){
        List<IItemMod> mods = new ItemCreator(itemStack).getItemMods();
        IItemMod miningSpeedMod = mods.stream().filter(iItemMod -> iItemMod instanceof IMiningSpeedModifier).findFirst().orElse(null);
        double miningSpeed = miningSpeedMod != null ? (double) miningSpeedMod.getLevel() / 100 : baseMiningSpeed;
        return Math.log10(miningSpeed) + 0.1d;
    }
}
