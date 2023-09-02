package net.riblab.tradecore.item.base;

import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.riblab.tradecore.general.NBTTagNames;
import net.riblab.tradecore.item.ItemCreator;
import net.riblab.tradecore.item.mod.IItemMod;
import net.riblab.tradecore.item.mod.ModDefaultMaxDurabilityI;
import net.riblab.tradecore.item.mod.ModRandomDurabilityI;
import net.riblab.tradecore.item.mod.ModMiningSpeedI;
import net.riblab.tradecore.modifier.IDurabilityModifier;
import net.riblab.tradecore.modifier.IMiningSpeedModifier;
import net.riblab.tradecore.modifier.IRandomItemModCreator;
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

    /**
     * 　固有アイテムの型を作成する
     */
    public TCTool(TextComponent name, Material material, String internalName, int customModelData, ToolType toolType, int harvestLevel, MiningSpeedTable miningSpeedTable, List<IItemMod<?>> defaultMods) {
        super(name, material, internalName, customModelData, defaultMods);

        this.toolType = toolType;
        this.harvestLevel = harvestLevel;
        this.miningSpeedTable = miningSpeedTable;
    }

    @Override
    public @Nonnull ItemStack getItemStack() {
        double miningSpeed = miningSpeedTable.getRandomMiningSpeed();
        IRandomItemModCreator mod = (IRandomItemModCreator) getDefaultMods().stream().filter(iItemMod -> iItemMod instanceof IRandomItemModCreator).findFirst().orElse(null);
        //TODO:randomModsがなかった時の処理
        List<IItemMod<?>> randomMods = new ArrayList<>();
        randomMods = mod.apply(randomMods, randomMods);
        List<IItemMod<?>> initMods = new ArrayList<>();
        initMods.add(new ModMiningSpeedI(miningSpeed));
        initMods.addAll(randomMods);

        return new ItemCreator(getTemplate().create())
                .setLores(getLore(initMods))
                .writeItemMods(initMods).create();
    }

    /**
     * ツールの説明を生成する
     *
     * @param randomMods ツールが持つランダムmod
     * @return ツールの説明
     */
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
    
    @Override
    public double getActualMiningSpeed(ItemStack itemStack){
        List<IItemMod<?>> mods = new ItemCreator(itemStack).getItemMods();
        IItemMod<?> miningSpeedMod = mods.stream().filter(iItemMod -> iItemMod instanceof IMiningSpeedModifier).findFirst().orElse(null);
        double miningSpeed = miningSpeedMod != null ? (double)miningSpeedMod.getParam() : miningSpeedTable.getMiddleMiningSpeed();
        return Math.log10(miningSpeed) + 0.1d;
    }
}
