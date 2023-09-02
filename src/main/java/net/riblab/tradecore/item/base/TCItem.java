package net.riblab.tradecore.item.base;


import de.exlll.configlib.Configuration;
import de.exlll.configlib.Ignore;
import lombok.Data;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.riblab.tradecore.general.NBTTagNames;
import net.riblab.tradecore.item.ItemCreator;
import net.riblab.tradecore.item.mod.IItemMod;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
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
    private int customModelData = 0;

    /**
     * アイテムが既定で持つ修飾子のリスト
     */
    @Ignore //TODO:シリアライズ、デシリアライズ可能にする
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
        return new ItemCreator(material)
                .setName(name.decoration(TextDecoration.ITALIC, false).color(NamedTextColor.WHITE))
                .setStrNBT(NBTTagNames.ITEMID.get(), internalName)
                .hideEnchantment()
                .setCustomModelData(customModelData)
                .setUnbreakable(false)
                .hideAttributes();
    }

    @Nonnull
    public ItemStack getTemplateItemStack() {
        if (itemStackTemplate == null)
            itemStackTemplate = getTemplate();

        return itemStackTemplate.create();
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
    public List<Component> getLore(List<IItemMod<?>> randomMods){
        return new ArrayList<>();
    }

    /**
     * ツールに元からあるmodの説明文を取得する
     */
    public List<TextComponent> getDefaultModsLore(){
        List<TextComponent> texts = new ArrayList<>();

        for (IItemMod<?> defaultMod : getDefaultMods()) {
            if(defaultMod.getLore() == null)
                continue;
            
            texts.add(Component.text(defaultMod.getLore()).decoration(TextDecoration.ITALIC, false).color(NamedTextColor.WHITE));
        }

        return texts;
    }
}
