package net.riblab.tradecore.modifier;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import net.riblab.tradecore.job.data.JobType;

/**
 * アイテムのツールとしての側面を定義するmod
 */
public interface IToolStatsModifier extends IModifier<IToolStatsModifier.ToolStats>{

    @Data
    @AllArgsConstructor
    class ToolStats{
        ToolType toolType;
        int harvestLevel;
    }

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
