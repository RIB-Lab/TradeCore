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
    private final List<IItemMod> defaultMods;

    @Getter
    private final IWeaponAttribute attribute;
    
    @Getter
    private final AttackDamageSpread attackDamageSpread;

    /**
     * 　固有アイテムの型を作成する
     */
    public TCWeapon(TextComponent name, Material material, String internalName, int customModelData, DurabilityTable durabilityTable, List<IItemMod> defaultMods, IWeaponAttribute attribute, AttackDamageSpread attackDamageSpread) {
        super(name, material, internalName, customModelData);
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

        List<IItemMod> initMods = List.of(
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
    protected List<Component> getLore(int durability, List<IItemMod> randomMods) {
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
     * 耐久値のツールチップを取得する
     */
    private TextComponent getDurabilityLore(int durability, List<IItemMod> randomMods){
        int maxDurability = getMaxDurability(randomMods);

        return Component.text("耐久値: ").decoration(TextDecoration.ITALIC, false).color(NamedTextColor.WHITE)
                .append(Component.text(durability).color(durability == maxDurability ? NamedTextColor.WHITE : NamedTextColor.YELLOW))
                .append(Component.text("/" + maxDurability).color(NamedTextColor.WHITE));
    }

    /**
     * ItemStackに付与されたmodから最大耐久値を割り出す
     */
    private int getMaxDurability(ItemStack instance){
        return getMaxDurability(new ItemCreator(instance).getItemMods());
    }
    
    /**
     * mod達の中から最大耐久値を持つmodを割出し、その値を返す
     */
    private int getMaxDurability(List<IItemMod> mods){
        IItemMod maxDurabilityMod = mods.stream().filter(iItemMod -> iItemMod instanceof IDurabilityModifier).findFirst().orElse(null);
        return maxDurabilityMod != null ? (int) maxDurabilityMod.getLevel() : durabilityTable.getMiddleMaxDurability(); //アイテムにランダムな最大耐久値が付与されていなかったらフォールバックとして基礎最大耐久値を使う
    }

    /**
     * ツールに元からあるmodの説明文を取得する
     */
    private List<TextComponent> getDefaultModsLore(){
        List<TextComponent> texts = new ArrayList<>();

        for (IItemMod defaultMod : defaultMods) {
            texts.add(Component.text(defaultMod.getLore()).decoration(TextDecoration.ITALIC, false).color(NamedTextColor.WHITE));
        }

        return texts;
    }

    /**
     * ツールに付与されているランダムmodの説明文を取得する
     */
    private List<TextComponent> getRandomModsLore(List<IItemMod> randomMods){
        List<TextComponent> texts = new ArrayList<>();

        for (IItemMod randomMod : randomMods) {
            if(randomMod instanceof IDurabilityModifier){//これだけ現在の耐久値を確認するため追加不可能
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

        int maxDurability = getMaxDurability(instance);

        if (durability > maxDurability) //耐久MAX
            durability = maxDurability;

        int damageToSet = (int) (instance.getType().getMaxDurability() * ((float) durability / (float) maxDurability));
        int damageToDeal = (instance.getType().getMaxDurability() - instance.getDurability()) - damageToSet;
        return new ItemCreator(instance).setLores(getLore(durability, new ItemCreator(instance).getItemMods())).damage(damageToDeal).setIntNBT(NBTTagNames.DURABILITY.get(), durability).create();
    }
}
