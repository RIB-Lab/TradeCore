package net.riblab.tradecore.item;

import lombok.Getter;
import net.kyori.adventure.text.TextComponent;
import net.riblab.tradecore.ItemCreator;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

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
        return super.createItem().setIntNBT(durabilityTag, baseDurability);
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
        return new ItemCreator(instance).damage(damageToDeal).setIntNBT(durabilityTag, durability).create();
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
