package net.riblab.tradecore.item.base;

import lombok.Getter;
import net.riblab.tradecore.job.data.JobData;

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

    default double getActualMiningSpeed() {
        return Math.log10(getBaseMiningSpeed()) + 0.1d;
    }

    /**
     * ツールの種類 TODO:SWORDを廃止してTCWeaponに移行
     */
    public enum ToolType {
        HAND(JobData.JobType.Mower),
        AXE(JobData.JobType.Woodcutter),
        PICKAXE(JobData.JobType.Miner),
        SHOVEL(JobData.JobType.Digger),
        HOE(null),
        SHEARS(null);


        @Getter
        private final JobData.JobType expType;

        ToolType(JobData.JobType expType){
            this.expType = expType;
        }
    }
}
