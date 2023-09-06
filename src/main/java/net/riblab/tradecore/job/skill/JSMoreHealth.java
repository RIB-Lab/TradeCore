/*
 * Copyright (c) 2023. RIBLaB 
 */
package net.riblab.tradecore.job.skill;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.riblab.tradecore.job.data.JobType;
import net.riblab.tradecore.modifier.IHPModifier;

import java.util.List;

public class JSMoreHealth extends JobSkill implements IHPModifier {

    public static final String name = "精霊の加護";
    public static final List<JobType> availableSkillType = List.of(JobType.WOODCUTTER);
    public static final List<Component> lore = List.of(Component.text("1レベルごとに最大HPが1増える").decoration(TextDecoration.ITALIC, false).color(NamedTextColor.GRAY));
    public static final int maxLevel = 10;

    @Override
    public Integer apply(Integer originalValue, Integer modifiedValue) {
        return modifiedValue + getLevel();
    }
}
