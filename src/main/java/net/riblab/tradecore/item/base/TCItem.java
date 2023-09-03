package net.riblab.tradecore.item.base;


import de.exlll.configlib.Configuration;
import de.exlll.configlib.Ignore;
import lombok.Data;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.riblab.tradecore.general.NBTTagNames;
import net.riblab.tradecore.item.ItemCreator;
import net.riblab.tradecore.item.mod.IItemMod;
import net.riblab.tradecore.item.mod.ModWeaponAttribute;
import net.riblab.tradecore.modifier.IItemTemplateModifier;
import net.riblab.tradecore.modifier.IRandomItemModCreator;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;

@Data
@Configuration
public class TCItem implements ITCItem {
    
    /**
     * アイテムの表示名。金床で変更できる
     */
    private TextComponent name = Component.text("dummy");

    /**
     * アイテムの元となるバニラアイテムの種類
     */
    private Material material = Material.COBBLESTONE;

    /**
     * アイテムのインスタンスのテンプレートのキャッシュ<br>
     * アイテムの唯一不変の実体で、固有のmodだけが実装されている<br>
     * 注意：絶対に変更してはいけないので必ずクローンして使うこと
     */
    @Ignore
    protected ItemCreator itemStackTemplate;

    /**
     * 主に召喚コマンドで用いられる内部的なアイテム名<br>
     */
    @Ignore
    private String internalName = "dummy";

    /**
     * アイテムのカスタムモデルデータ(0に設定した場合なし)
     */
    @Getter
    private int customModelData = 0;

    /**
     * アイテムが既定で持つ修飾子のリスト
     */
    private List<IItemMod<?>> defaultMods = new ArrayList<>();

    /**
     * 　固有アイテムの型を作成する
     *
     * @param name            作りたい固有アイテムの名前(ユーザーが読むので必ず日本語にすること)
     * @param material        作りたい固有アイテムの元となるバニラアイテム
     * @param internalName    作りたい固有アイテムの内部的な名前<br>
     *                        召喚コマンドで使われるので必ず半角英数字にしてスペースの代わりに_を使うこと
     * @param customModelData 固有アイテムにセットするカスタムモデルデータ
     */
    @ParametersAreNonnullByDefault
    public TCItem(TextComponent name, Material material, String internalName, int customModelData, List<IItemMod<?>> defaultMods) {
        this.name = name;
        this.material = material;
        this.internalName = internalName;
        this.customModelData = customModelData;
        this.defaultMods = defaultMods;
    }

    /**
     * データ読み取り用
     */
    public TCItem(){}

    /**
     * 固有アイテムの型の実体を作製する内部的な関数
     *
     * @return 作られたアイテムの型の実体
     */
    protected @Nonnull ItemCreator getTemplate() {
        ModWeaponAttribute speedMod = (ModWeaponAttribute) getDefaultMods().stream().filter(iItemMod -> iItemMod instanceof ModWeaponAttribute).findFirst().orElse(null);
        ItemCreator template = new ItemCreator(material)
                .setName(name.decoration(TextDecoration.ITALIC, false).color(NamedTextColor.WHITE))
                .setLores(getDefaultModsLore())
                .setStrNBT(NBTTagNames.ITEMID.get(), internalName)
                .hideEnchantment()
                .setCustomModelData(customModelData)
                .setUnbreakable(false)
                .setAttackSpeedAttr(speedMod)
                .hideAttributes();

        ItemStack templateStack = template.create();
        List<IItemTemplateModifier> mods = getDefaultMods().stream().filter(iItemMod -> iItemMod instanceof IItemTemplateModifier).map(iItemMod -> (IItemTemplateModifier) iItemMod).toList();
        for (IItemTemplateModifier mod : mods) {
            templateStack = mod.apply(templateStack, templateStack);
        }
        return new ItemCreator(templateStack);
    }

    @Nonnull
    public ItemStack getTemplateItemStack() {
        if (itemStackTemplate == null)
            itemStackTemplate = getTemplate();

        return itemStackTemplate.create();
    }


    @Override
    public @Nonnull ItemStack getItemStack() {
        List<IRandomItemModCreator> mods = getDefaultMods().stream().filter(iItemMod -> iItemMod instanceof IRandomItemModCreator).map(iItemMod -> (IRandomItemModCreator) iItemMod).toList();
        List<IItemMod<?>> randomMods = new ArrayList<>();
        for (IRandomItemModCreator mod : mods) {
            randomMods = mod.apply(randomMods, randomMods);
        }

        List<IItemMod<?>> initMods = new ArrayList<>(randomMods);

        return new ItemCreator(getTemplate().create())
                .setLores(getLore(initMods))
                .writeItemRandomMods(initMods).create();
    }


    @Override
    public boolean isSimilar(@Nullable ItemStack itemStack) {
        if (itemStack == null || itemStack.getType().equals(Material.AIR))
            return false;

        String ID = new ItemCreator(itemStack).getStrNBT(NBTTagNames.ITEMID.get());

        if (ID == null)
            return false;

        return ID.equals(internalName);
    }

    @Override
    public boolean isSimilar(@Nullable String tcID) {
        if (tcID == null)
            return false;

        return tcID.equals(internalName);
    }


    @Override
    public List<Component> getLore(List<IItemMod<?>> randomMods) {
        List<Component> texts = new ArrayList<>();
        texts.addAll(getDefaultModsLore());
        texts.addAll(getRandomModsLore(randomMods));
        return texts;
    }

    /**
     * ツールに元からあるmodの説明文を取得する
     */
    public List<Component> getDefaultModsLore(){
        List<Component> texts = new ArrayList<>();

        for (IItemMod<?> defaultMod : getDefaultMods()) {
            if(defaultMod.getLore() == null)
                continue;
            
            texts.add(Component.text(defaultMod.getLore()).decoration(TextDecoration.ITALIC, false).color(NamedTextColor.WHITE));
        }

        return texts;
    }

    /**
     * ツールに付与されているランダムmodの説明文を取得する
     */
    public List<Component> getRandomModsLore(List<IItemMod<?>> randomMods){
        List<Component> texts = new ArrayList<>();

        for (IItemMod<?> randomMod : randomMods) {
            if(randomMod.getLore() != null)
                texts.add(Component.text(randomMod.getLore()).decoration(TextDecoration.ITALIC, false).color(NamedTextColor.WHITE));
        }

        return texts;
    }
}
