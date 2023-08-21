package net.riblab.tradecore.job.skill;

import lombok.Data;
import net.riblab.tradecore.craft.TCCraftingRecipe;

/**
 * クラフトの所要金額に影響を与えるスキル
 */
public interface ICraftFeeModifier extends IModifier<ICraftFeeModifier.PackedCraftFee>{
    
    @Data
    public class PackedCraftFee{
        TCCraftingRecipe recipe;
        double fee;
    }
}