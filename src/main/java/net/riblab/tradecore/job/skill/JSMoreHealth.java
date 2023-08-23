package net.riblab.tradecore.job.skill;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.riblab.tradecore.job.JobData;
import net.riblab.tradecore.job.JobSkill;
import net.riblab.tradecore.modifier.IHPModifier;
import net.riblab.tradecore.modifier.IJobExpModifier;

import java.util.List;
import java.util.Random;

public class JSMoreHealth extends JobSkill implements IHPModifier {

    public static final String name = "精霊の加護";
    public static final List<JobData.JobType> availableSkillType = List.of(JobData.JobType.Woodcutter);
    public static final List<Component> lore = List.of(Component.text("1レベルごとに最大HPが1増える").decoration(TextDecoration.ITALIC, false).color(NamedTextColor.GRAY));
    public static final int maxLevel = 10;

    @Override
    public Integer apply(Integer originalValue, Integer modifiedValue) {
        return modifiedValue + getLevel();
    }
}