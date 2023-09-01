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
     * ツールの基礎採掘速度(1が素手と同じ速さで、10000000000で1tick破壊)
     */
    double getBaseMiningSpeed();

    /**
     * ツールを一振りしたらどれくらい亀裂が入るかの実際の値
     */
    double getActualMiningSpeed(ItemStack itemStack);
    
    //TODO:tier別で分離
    double mineSpeedRandomness = 0.05d;
    
    int maxDurabilityRandomness = 10;

    /**
     * ツールの種類
     */
    enum ToolType {
        HAND(JobType.Mower),
        AXE(JobType.Woodcutter),
        PICKAXE(JobType.Miner),
        SHOVEL(JobType.Digger),
        HOE(null),
        SHEARS(null);


        @Getter
        private final JobType expType;

        ToolType(JobType expType) {
            this.expType = expType;
        }
    }
}
