package net.riblab.tradecore.job;

import lombok.Getter;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum JobSkills {
    RESOURCEGATHERING(JSResourceGathering.class),
    CRAFTCOST(JSReduceCraftCost.class);

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
     * @return
     */
    public static String getSkillName(Class<? extends JobSkill> skillType){
        Field field = null;
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
}