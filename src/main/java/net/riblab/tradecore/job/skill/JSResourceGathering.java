package net.riblab.tradecore.job.skill;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.riblab.tradecore.job.JobData;
import net.riblab.tradecore.job.JobSkill;

import java.util.List;

public class JSResourceGathering extends JobSkill implements IResourceChanceModifier {

    public static final String name = "資源採取効率強化";
    public static final List<JobData.JobType> availableSkillType = List.of(JobData.JobType.Miner, JobData.JobType.Woodcutter, JobData.JobType.Digger);
    public static final List<Component> lore = List.of(Component.text("1レベルごとに全ての資源の採取量が1%増加する").decoration(TextDecoration.ITALIC, false).color(NamedTextColor.GRAY));

    @Override
    public Float apply(Float originalChance, Float modifiedChance) {
        return modifiedChance + originalChance * (getLevel() * 0.01f);
    }
}