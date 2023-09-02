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

/**
 * プラグインのツールクラス
 */
public class TCTool extends TCItem implements ITCTool {

    @Getter
    private final ToolType toolType;

    @Getter
    private final int harvestLevel;

    @Getter
    private final MiningSpeedTable miningSpeedTable;

    @Getter
    private final DurabilityTable durabilityTable;

    /**
     * 　固有アイテムの型を作成する
     */
    public TCTool(TextComponent name, Material material, String internalName, int customModelData, ToolType toolType, int harvestLevel, MiningSpeedTable miningSpeedTable, DurabilityTable durabilityTable, List<IItemMod<?>> defaultMods) {
        super(name, material, internalName, customModelData, defaultMods);

        this.toolType = toolType;
        this.harvestLevel = harvestLevel;
        this.miningSpeedTable = miningSpeedTable;
        this.durabilityTable = durabilityTable;
    }

    @Override
    protected @Nonnull ItemCreator getTemplate() {
        return super.getTemplate().setIntNBT(NBTTagNames.DURABILITY.get(), durabilityTable.getMiddleMaxDurability())
                .setLores(getLore(durabilityTable.getMiddleMaxDurability(), new ArrayList<>()));
    }

    @Override
    public @Nonnull ItemStack getItemStack() {
        double miningSpeed = miningSpeedTable.getRandomMiningSpeed();
        int maxDurability = durabilityTable.getRandomMaxDurability();
        
        List<IItemMod<?>> initMods = List.of(
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
    public List<Component> getLore(int durability, List<IItemMod<?>> randomMods) {
        List<Component> texts = new ArrayList<>();
        if (durabilityTable.getMiddleMaxDurability() != -1) {
            texts.add(getDurabilityLore(durability, randomMods));
        }
        
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
            if(randomMod instanceof IDurabilityModifier){//これだけ現在の耐久値を確認するため追加不可能
                continue;
            }

            texts.add(Component.text(randomMod.getLore()).decoration(TextDecoration.ITALIC, false).color(NamedTextColor.WHITE));
        }

        return texts;
    }
    
    @Override
    public double getActualMiningSpeed(ItemStack itemStack){
        List<IItemMod<?>> mods = new ItemCreator(itemStack).getItemMods();
        IItemMod<?> miningSpeedMod = mods.stream().filter(iItemMod -> iItemMod instanceof IMiningSpeedModifier).findFirst().orElse(null);
        double miningSpeed = miningSpeedMod != null ? (double)miningSpeedMod.getParam() : miningSpeedTable.getMiddleMiningSpeed();
        return Math.log10(miningSpeed) + 0.1d;
    }
}
