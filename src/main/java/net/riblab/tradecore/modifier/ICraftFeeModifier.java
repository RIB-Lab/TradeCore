package net.riblab.tradecore.modifier;

import lombok.Data;
import net.riblab.tradecore.craft.ITCCraftingRecipe;

/**
 * クラフトの所要金額に影響を与えるスキル
 */
public interface ICraftFeeModifier extends IModifier<ICraftFeeModifier.PackedCraftFee>{
    
    @Data
    public class PackedCraftFee{
        ITCCraftingRecipe recipe;
        double fee;
    }
}