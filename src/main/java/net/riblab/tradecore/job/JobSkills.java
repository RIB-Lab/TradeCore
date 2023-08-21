package net.riblab.tradecore.job;

import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.riblab.tradecore.job.skill.*;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum JobSkills {
    RESOURCEGATHERING(JSResourceGathering.class),
    CRAFTCOST(JSReduceCraftCost.class),
    CHEAPERFUELBALL(JSCheaperFuelBall.class),
    BETTERHANDATTACK(JSBetterHandAttack.class),
    BETTERTRAINING(JSBetterTraining.class),
    HEAVYLIFTING(JSHeavyLifting.class),
    MINERALARMOR(JSMineralArmor.class),
    FREESTICK(JSFreeStick.class);

    @Getter
    private final Class<? extends JobSkill> skillType;

    JobSkills(Class<? extends JobSkill> skiiType) {
        this.skillType = skiiType;
    }

    /**
     * あるクラスで習得可能な全てのスキルタイプを列挙
     * @param jobType ジョブの種類
     * @return 習得可能なスキルのリスト
     */
    public static List<Class<? extends JobSkill>> getAvailableSkills(JobData.JobType jobType){
        return Arrays.stream(JobSkills.values()).map(jobSkills -> jobSkills.skillType).filter(skillType -> {
            List<JobData.JobType> skillTypes = new ArrayList<>();
            try {
                Field field = skillType.getDeclaredField("availableSkillType");
                skillTypes = (List<JobData.JobType>)field.get(null);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
            return skillTypes.contains(jobType);
        }).collect(Collectors.toList());
    }

    /**
     * あるスキルの名前を取得
     */
    public static String getSkillName(Class<? extends JobSkill> skillType){
        Field field;
        try {
            field = skillType.getDeclaredField("name");
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
        try {
            return (String) field.get(null);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * あるスキルの解説文を取得
     */
    public static List<Component> getSkillLore(Class<? extends JobSkill> skillType){
        Field field;
        try {
            field = skillType.getDeclaredField("lore");
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
        try {
            return (List<Component>) field.get(null);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * あるスキルの最大レベルを取得
     */
    public static int getMaxLevel(Class<? extends JobSkill> skillType){
        Field field;
        try {
            field = skillType.getDeclaredField("maxLevel");
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
        try {
            return (int) field.get(null);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
