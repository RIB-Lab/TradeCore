package net.riblab.tradecore.item.base;

import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.riblab.tradecore.general.NBTTagNames;
import net.riblab.tradecore.item.ItemCreator;
import net.riblab.tradecore.item.mod.IItemMod;
import net.riblab.tradecore.item.mod.ModAttackDamageI;
import net.riblab.tradecore.item.mod.ModMaxDurabilityI;
import net.riblab.tradecore.modifier.IDurabilityModifier;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class TCWeapon extends TCItem implements ITCWeapon {

    @Getter
    private final DurabilityTable durabilityTable;

    @Getter
    private final List<IItemMod<?>> defaultMods;

    @Getter
    private final IWeaponAttribute attribute;
    
    @Getter
    private final AttackDamageSpread attackDamageSpread;

    /**
     * 　固有アイテムの型を作成する
     */
    public TCWeapon(TextComponent name, Material material, String internalName, int customModelData, DurabilityTable durabilityTable, List<IItemMod<?>> defaultMods, IWeaponAttribute attribute, AttackDamageSpread attackDamageSpread) {
        super(name, material, internalName, customModelData, defaultMods);
        this.durabilityTable = durabilityTable;
        this.defaultMods = defaultMods;
        this.attribute = attribute;
        this.attackDamageSpread = attackDamageSpread;
    }


    @Override
    protected @Nonnull ItemCreator getTemplate() {
        return super.getTemplate().setIntNBT(NBTTagNames.DURABILITY.get(), durabilityTable.getMiddleMaxDurability())
                .setLores(getLore(durabilityTable.getMiddleMaxDurability(), new ArrayList<>())).setAttackSpeedAttr(attribute.getAttackSpeed());
    }

    @Override
    public @Nonnull ItemStack getItemStack() {
        int maxDurability = durabilityTable.getRandomMaxDurability();
        double attackDamage = attackDamageSpread.getRandomDamage(attribute.getBaseAttackDamage());

        List<IItemMod<?>> initMods = List.of(
                new ModMaxDurabilityI(maxDurability),
                new ModAttackDamageI((int) (attackDamage * 100)));

        return new ItemCreator(getTemplate().create())
                .setIntNBT(NBTTagNames.DURABILITY.get(), maxDurability)
                .setLores(getLore(maxDurability, initMods))
                .writeItemMods(initMods).create();
    }


    /**
     * 武器の説明を生成する
     *
     * @param durability インスタンスが持つ耐久値
     * @return ツールの説明
     */
    public List<Component> getLore(int durability, List<IItemMod<?>> randomMods) {
        List<Component> texts = new ArrayList<>();
        if (durabilityTable.getMiddleMaxDurability() != -1) {
            texts.add(getDurabilityLore(durability, randomMods));
        }
        texts.add(Component.text("攻撃速度: " + Math.floor(attribute.getAttackSpeedForDisplay() * 100) / 100).decoration(TextDecoration.ITALIC, false).color(NamedTextColor.WHITE));
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
}
