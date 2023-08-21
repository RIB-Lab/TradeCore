package net.riblab.tradecore.job.skill;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.riblab.tradecore.job.JobData;
import net.riblab.tradecore.job.JobSkill;

import java.util.List;

public class JSReduceCraftCost extends JobSkill implements ICraftFeeModifier {

    public static final String name = "交渉術";
    public static final List<JobData.JobType> availableSkillType = List.of(JobData.JobType.Crafter);
    public static final List<Component> lore = List.of(Component.text("1レベルごとにクラフトの必要金額が5%減少する").decoration(TextDecoration.ITALIC, false).color(NamedTextColor.GRAY));

    @Override
    public PackedCraftFee apply(PackedCraftFee originalFee, PackedCraftFee modifiedFee) {
        PackedCraftFee newFee = new PackedCraftFee();
        newFee.setRecipe(modifiedFee.getRecipe());
        newFee.setFee(modifiedFee.getFee() * (1 - getLevel() * 0.05));
        return newFee;
    }
}
