/*
 * Copyright (c) 2023. RIBLaB 
 */
package net.riblab.tradecore.job.skill;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.riblab.tradecore.job.data.JobType;
import net.riblab.tradecore.modifier.ICraftFeeModifier;

import java.util.List;
import java.util.Set;

public class JSFreeStick extends JobSkill implements ICraftFeeModifier {

    public static final String name = "手斧";
    public static final List<JobType> availableSkillType = List.of(JobType.WOODCUTTER);
    public static final List<Component> lore = List.of(Component.text("2ポイント振ると棒系レシピの必要金額が0になる").decoration(TextDecoration.ITALIC, false).color(NamedTextColor.GRAY));
    public static final int maxLevel = 2;

    private static final Set<String> appliedRecipes = Set.of("stick", "reinforced_stick");

    @Override
    public PackedCraftFee apply(PackedCraftFee originalFee, PackedCraftFee modifiedFee) {
        PackedCraftFee newFee = new PackedCraftFee();
        newFee.setRecipe(modifiedFee.getRecipe());
        if (appliedRecipes.contains(modifiedFee.getRecipe().getResult()) && getLevel() >= 2) {
            newFee.setFee(0);
        } else {
            newFee.setFee(modifiedFee.getFee());
        }
        return newFee;
    }
}
