/*
 * Copyright (c) 2023. RIBLaB 
 */
package net.riblab.tradecore.job.skill;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.riblab.tradecore.job.data.JobType;
import net.riblab.tradecore.modifier.IResourceChanceModifier;

import java.util.List;

public class JSResourceGathering extends JobSkill implements IResourceChanceModifier {

    public static final String name = "資源採取効率強化";
    public static final List<JobType> availableSkillType = List.of(JobType.MINER, JobType.WOODCUTTER, JobType.DIGGER);
    public static final List<Component> lore = List.of(Component.text("1レベルごとに全ての資源の採取確率が1%増加する").decoration(TextDecoration.ITALIC, false).color(NamedTextColor.GRAY));
    public static final int maxLevel = 10;

    @Override
    public Float apply(Float originalChance, Float modifiedChance) {
        return modifiedChance + originalChance * (getLevel() * 0.01f);
    }
}