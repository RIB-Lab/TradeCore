package net.riblab.tradecore.item.mod;

import net.riblab.tradecore.modifier.ICraftFeeModifier;

public class ModReduceCraftCostI extends ItemMod implements ICraftFeeModifier {

    public ModReduceCraftCostI(int level) {
        super(level);
    }

    @Override
    public String getLore() {
        return "クラフトコスト: -" + getLevel() + "%";
    }

    @Override
    public PackedCraftFee apply(PackedCraftFee originalValue, PackedCraftFee modifiedValue) {
        PackedCraftFee newFee = new PackedCraftFee();
        newFee.setRecipe(modifiedValue.getRecipe());
        newFee.setFee(modifiedValue.getFee() - originalValue.getFee() * 0.01 * getLevel());
        return newFee;
    }
}
