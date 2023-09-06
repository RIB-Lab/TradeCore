/*
 * Copyright (c) 2023. RIBLaB 
 */
package net.riblab.tradecore.item.mod;

import net.riblab.tradecore.modifier.ICraftFeeModifier;

public class ModAddCraftCostI extends ItemMod<Integer> implements ICraftFeeModifier {

    public ModAddCraftCostI(Integer level) {
        super(level);
    }

    @Override
    public String getLore() {
        if(getParam() >= 0){
            return "クラフトコスト: +" + this.getParam() + "%";
        }
        else{
            return "クラフトコスト: " + this.getParam() + "%";
        }
    }

    @Override
    public PackedCraftFee apply(PackedCraftFee originalValue, PackedCraftFee modifiedValue) {
        PackedCraftFee newFee = new PackedCraftFee();
        newFee.setRecipe(modifiedValue.getRecipe());
        newFee.setFee(modifiedValue.getFee() + originalValue.getFee() * 0.01 * this.getParam());
        return newFee;
    }
}
