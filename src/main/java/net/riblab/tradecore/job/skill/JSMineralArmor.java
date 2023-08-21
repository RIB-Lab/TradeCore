package net.riblab.tradecore.job.skill;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.riblab.tradecore.job.JobData;
import net.riblab.tradecore.job.JobSkill;

import java.util.List;

public class JSMineralArmor extends JobSkill implements IArmorModifier {

    public static final String name = "ミネラルアーマー";
    public static final List<JobData.JobType> availableSkillType = List.of(JobData.JobType.Miner);
    public static final List<Component> lore = List.of(Component.text("1レベルごとに装備から得られるアーマー値が10%上昇する").decoration(TextDecoration.ITALIC, false).color(NamedTextColor.GRAY));

    @Override
    public Double apply(Double originalValue, Double modifiedValue) {
        return modifiedValue + originalValue * (getLevel() * 0.1);
    }
}
