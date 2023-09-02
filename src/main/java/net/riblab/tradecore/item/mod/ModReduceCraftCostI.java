package net.riblab.tradecore.item.mod;

import net.riblab.tradecore.modifier.ICraftFeeModifier;

public class ModReduceCraftCostI extends ItemMod<Integer> implements ICraftFeeModifier {

    public ModReduceCraftCostI(Integer level) {
        super(level);
    }

    @Override
    public String getLore() {
        return "クラフトコスト: -" + this.getParam() + "%";
    }

    @Override
    public PackedCraftFee apply(PackedCraftFee originalValue, PackedCraftFee modifiedValue) {
        PackedCraftFee newFee = new PackedCraftFee();
        newFee.setRecipe(modifiedValue.getRecipe());
        newFee.setFee(modifiedValue.getFee() - originalValue.getFee() * 0.01 * this.getParam());
        return newFee;
    }
}
