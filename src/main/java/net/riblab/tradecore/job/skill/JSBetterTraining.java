package net.riblab.tradecore.job.skill;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.riblab.tradecore.job.data.JobType;
import net.riblab.tradecore.modifier.IJobExpModifier;

import java.util.List;
import java.util.Random;

public class JSBetterTraining extends JobSkill implements IJobExpModifier {

    public static final String name = "工芸は全ての職の土台";
    public static final List<JobType> availableSkillType = List.of(JobType.CRAFTER);
    public static final List<Component> lore = List.of(Component.text("1レベルごとに20%の確率でジョブ経験値を取得した時追加で1もらえる").decoration(TextDecoration.ITALIC, false).color(NamedTextColor.GRAY));
    public static final int maxLevel = 5;

    @Override
    public Integer apply(Integer originalValue, Integer modifiedValue) {
        float chance = new Random().nextFloat();
        if (chance < (getLevel() * 0.2)) {
            return modifiedValue + 1;
        }
        return modifiedValue;
    }
}
