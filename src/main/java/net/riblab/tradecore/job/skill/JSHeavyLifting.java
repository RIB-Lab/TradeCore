package net.riblab.tradecore.job.skill;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.riblab.tradecore.job.JobData;
import net.riblab.tradecore.job.JobSkill;

import java.util.List;

/**
 * ツールで敵を殴れるようになる
 */
public class JSHeavyLifting extends JobSkill implements ICanHitWithToolModifier {

    public static final String name = "ヘビーリフティング";
    public static final List<JobData.JobType> availableSkillType = List.of(JobData.JobType.Digger);
    public static final List<Component> lore = List.of(Component.text("3レベル振ると武器以外のツールで敵を攻撃できるようになる").decoration(TextDecoration.ITALIC, false).color(NamedTextColor.GRAY));
    
    @Override
    public Boolean apply(Boolean originalValue, Boolean modifiedValue) {
        if(getLevel() >= 3)
            return true;
        else return modifiedValue;
    }
}
