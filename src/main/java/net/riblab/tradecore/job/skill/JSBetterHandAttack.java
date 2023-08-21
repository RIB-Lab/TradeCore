package net.riblab.tradecore.job.skill;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.riblab.tradecore.job.JobData;
import net.riblab.tradecore.job.JobSkill;

import java.util.List;

public class JSBetterHandAttack extends JobSkill implements IHandAttackDamageModifier {

    public static final String name = "鍛えられた右手";
    public static final List<JobData.JobType> availableSkillType = List.of(JobData.JobType.Mower);
    public static final List<Component> lore = List.of(Component.text("1レベルごとに素手で殴るダメージが1アップ").decoration(TextDecoration.ITALIC, false).color(NamedTextColor.GRAY));
    public static final int maxLevel = 10;
    
    @Override
    public Double apply(Double originalValue, Double modifiedValue) {
        modifiedValue += getLevel() * 1d;
        return modifiedValue;
    }
}
