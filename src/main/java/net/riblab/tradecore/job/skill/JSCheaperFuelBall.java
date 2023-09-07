/*
 * Copyright (c) 2023. RIBLaB
 */
package net.riblab.tradecore.job.skill;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.riblab.tradecore.job.data.JobType;
import net.riblab.tradecore.modifier.IIngredientAmountModifier;

import java.util.List;

/**
 * 燃料玉レシピのコストを安くするスキル
 */
public class JSCheaperFuelBall extends JobSkill implements IIngredientAmountModifier {

    public static final String name = "草の達人";
    public static final List<JobType> availableSkillType = List.of(JobType.MOWER);
    public static final List<Component> lore = List.of(Component.text("1レベルごとに燃料玉を作るのに必要な干し草の量が1減る").decoration(TextDecoration.ITALIC, false).color(NamedTextColor.GRAY));
    public static final int maxLevel = 10;

    @Override
    public PackedRecipeData apply(PackedRecipeData originalValue, PackedRecipeData modifiedValue) {
        if (!originalValue.getRecipe().getResult().equals("fuel_ball"))
            return modifiedValue;

        int newAmount = modifiedValue.getAmount() - getLevel();
        newAmount = Math.max(newAmount, 1);
        modifiedValue.setAmount(newAmount);
        return modifiedValue;
    }
}
