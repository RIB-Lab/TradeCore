package net.riblab.tradecore.item;

import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.riblab.tradecore.ItemCreator;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

/**
 * プラグインのツールクラス
 */
public class TCTool extends TCItem{

    /**
     * ツールの種類
     */
    @Getter
    private final ToolType toolType;

    /**
     * ツールの採掘レベル
     */
    @Getter
    private final int harvestLevel;

    /**
     * ツールの基礎採掘速度(1が素手と同じ速さで、10000000000で1tick破壊)
     */
    @Getter
    private final double baseMiningSpeed;

    /**
     * ツールの基礎耐久値。-1で無限
     */
    @Getter
    private final int baseDurability;
    
    private static final String durabilityTag = "durability";
    
    /**
     * 　固有アイテムの型を作成する
     *
     * @param name            作りたい固有アイテムの名前(ユーザーが読むので必ず日本語にすること)
     * @param material        作りたい固有アイテムの元となるバニラアイテム
     * @param internalName    作りたい固有アイテムの内部的な名前<br>
     *                        召喚コマンドで使われるので必ず半角英数字にしてスペースの代わりに_を使うこと
     * @param customModelData 固有アイテムにセットするカスタムモデルデータ
     */
    public TCTool(TextComponent name, Material material, String internalName, int customModelData, ToolType toolType, int harvestLevel, double miningSpeed, int baseDurability) {
        super(name, material, internalName, customModelData);
        
        this.toolType = toolType;
        this.harvestLevel = harvestLevel;
        this.baseMiningSpeed = miningSpeed;
        this.baseDurability = baseDurability;
    }
    
    public double getActualMiningSpeed(){
        return Math.log10(baseMiningSpeed) + 0.1d;
    }
    
    @Override
    protected ItemCreator createItem() {
        return super.createItem().setIntNBT(durabilityTag, baseDurability)
                .setLores(getLore(baseDurability));
    }

    /**
     * ツールの説明を生成する
     * @param durability インスタンスが持つ耐久値
     * @return ツールの説明
     */
    protected List<Component> getLore(int durability){
        List<Component> texts = new ArrayList<>();
        if(baseDurability != -1){
            texts.add(Component.text("耐久値: ").decoration(TextDecoration.ITALIC, false).color(NamedTextColor.WHITE)
                    .append(Component.text(durability).color(durability == baseDurability ? NamedTextColor.WHITE : NamedTextColor.YELLOW))
                    .append(Component.text("/" + baseDurability).color(NamedTextColor.WHITE)));
        }
        texts.add(Component.text("採掘速度: ").decoration(TextDecoration.ITALIC, false).color(NamedTextColor.WHITE)
                .append(Component.text((Math.floor(baseMiningSpeed * 100)) / 100)));
        return texts;
    }

    /**
     * ツールのインスタンスの耐久値を1減らす
     * @param instance
     * @return
     */
    public ItemStack reduceDurability(ItemStack instance){
        if(!isSimilar(instance))
            return null;

        Integer nbt = new ItemCreator(instance).getIntNBT(durabilityTag);
        int durability = nbt;

        if(durability == -1) //耐久無限
            return instance;
        
        durability--;
        if(durability <= 0) //耐久切れ
            return null;
        
        int damageToSet = (int)(instance.getType().getMaxDurability() * ((float)durability / (float) baseDurability));
        int damageToDeal = (instance.getType().getMaxDurability() - instance.getDurability()) - damageToSet;
        return new ItemCreator(instance).setLores(getLore(durability)).damage(damageToDeal).setIntNBT(durabilityTag, durability).create();
    }

    public enum ToolType {
        HAND,
        AXE,
        PICKAXE,
        SHOVEL,
        HOE,
        SWORD,
        SHEARS
    }
}
