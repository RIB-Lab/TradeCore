/*
 * Copyright (c) 2023. RIBLaB 
 */
package net.riblab.tradecore.job.skill;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.riblab.tradecore.job.data.JobType;
import net.riblab.tradecore.modifier.ICanHitWithToolModifier;

import java.util.List;

/**
 * ツールで敵を殴れるようになる
 */
public class JSHeavyLifting extends JobSkill implements ICanHitWithToolModifier {

    public static final String name = "ヘビーリフティング";
    public static final List<JobType> availableSkillType = List.of(JobType.DIGGER);
    public static final List<Component> lore = List.of(Component.text("3レベル振ると武器以外のツールで敵を攻撃できるようになる").decoration(TextDecoration.ITALIC, false).color(NamedTextColor.GRAY));
    public static final int maxLevel = 3;

    @Override
    public Boolean apply(Boolean originalValue, Boolean modifiedValue) {
        if (getLevel() >= 3)
            return true;
        else return modifiedValue;
    }
}
