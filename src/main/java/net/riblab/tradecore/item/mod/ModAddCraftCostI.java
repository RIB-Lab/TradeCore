/*
 * Copyright (c) 2023. RIBLaB 
 */
package net.riblab.tradecore.item.mod;

import net.riblab.tradecore.modifier.ICraftFeeModifier;

import java.util.Optional;

public class ModAddCraftCostI extends ItemMod<Integer> implements ICraftFeeModifier {

    public ModAddCraftCostI(Integer level) {
        super(level);
    }

    @Override
    public Optional<String> getLore() {
        if(getParam() >= 0){
            return Optional.of("クラフトコスト: +" + this.getParam() + "%");
        }
        else{
            return Optional.of("クラフトコスト: " + this.getParam() + "%");
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
