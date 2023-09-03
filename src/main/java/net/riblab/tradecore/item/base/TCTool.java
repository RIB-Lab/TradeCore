package net.riblab.tradecore.item.base;

import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.riblab.tradecore.item.ItemCreator;
import net.riblab.tradecore.item.mod.IItemMod;
import net.riblab.tradecore.item.mod.ModRandomMiningSpeedI;
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

    /**
     * 　固有アイテムの型を作成する
     */
    public TCTool(TextComponent name, Material material, String internalName, int customModelData, ToolType toolType, int harvestLevel, List<IItemMod<?>> defaultMods) {
        super(name, material, internalName, customModelData, defaultMods);

        this.toolType = toolType;
        this.harvestLevel = harvestLevel;
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
                .writeItemRandomMods(initMods).create();
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
}
