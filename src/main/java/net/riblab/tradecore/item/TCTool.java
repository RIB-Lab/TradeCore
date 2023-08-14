package net.riblab.tradecore.item;

import lombok.Getter;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.Material;

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
     * ツールの採掘速度(1が素手と同じ速さで、10000000000で1tick破壊)
     */
    @Getter
    private final double baseMiningSpeed;
    
    /**
     * 　固有アイテムの型を作成する
     *
     * @param name            作りたい固有アイテムの名前(ユーザーが読むので必ず日本語にすること)
     * @param material        作りたい固有アイテムの元となるバニラアイテム
     * @param internalName    作りたい固有アイテムの内部的な名前<br>
     *                        召喚コマンドで使われるので必ず半角英数字にしてスペースの代わりに_を使うこと
     * @param customModelData 固有アイテムにセットするカスタムモデルデータ
     */
    public TCTool(TextComponent name, Material material, String internalName, int customModelData, ToolType toolType, int harvestLevel, double miningSpeed) {
        super(name, material, internalName, customModelData);
        
        this.toolType = toolType;
        this.harvestLevel = harvestLevel;
        this.baseMiningSpeed = miningSpeed;
    }
    
    public double getActualMiningSpeed(){
        return Math.log10(baseMiningSpeed) + 0.1d;
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
