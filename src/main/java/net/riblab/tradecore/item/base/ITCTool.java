package net.riblab.tradecore.item.base;

import lombok.Getter;
import net.riblab.tradecore.job.data.JobType;
import org.bukkit.inventory.ItemStack;

public interface ITCTool extends IHasDurability, IHasItemMod {

    /**
     * ツールの種類
     */
    ToolType getToolType();

    /**
     * ツールの採掘レベル
     */
    int getHarvestLevel();

    /**
     * ツールの属する採掘速度テーブル
     */
    MiningSpeedTable getMiningSpeedTable();

    /**
     * ツールを一振りしたらどれくらい亀裂が入るかの実際の値
     */
    double getActualMiningSpeed(ItemStack itemStack);

    /**
     * ツールの種類
     */
    enum ToolType {
        HAND(JobType.MOWER),
        AXE(JobType.WOODCUTTER),
        PICKAXE(JobType.MINER),
        SHOVEL(JobType.DIGGER),
        HOE(null),
        SHEARS(null);


        @Getter
        private final JobType expType;

        ToolType(JobType expType) {
            this.expType = expType;
        }
    }
}
