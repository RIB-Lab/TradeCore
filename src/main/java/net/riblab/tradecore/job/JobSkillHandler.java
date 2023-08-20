package net.riblab.tradecore.job;

import net.kyori.adventure.text.Component;
import net.riblab.tradecore.TradeCore;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * スキルデータベースの操作クラス
 */
public class JobSkillHandler {

    private final Map<UUID, List<JobSkill>> datasMap = TradeCore.getInstance().getConfigManager().getJobDatas().playerJobSkills;

    /**
     * プレイヤーのJobスキルデータを初期化する
     */
    public void resetPlayerJobSkillData(OfflinePlayer offlinePlayer) {
        UUID uuid = offlinePlayer.getUniqueId();
        List<JobSkill> datas = datasMap.get(uuid);
        if(datas != null)
            datas.clear();
        else{
            datasMap.put(uuid, new ArrayList<>());
        }
    }

    /**
     * プレイヤーがまだ消費していないスキルポイントの数を取得する
     */
    public int getUnSpentSkillPoints(OfflinePlayer offlinePlayer, JobData.JobType type){
        return TradeCore.getInstance().getJobHandler().getJobData(offlinePlayer, type).getLevel() / 10 - getLearntSkillCount(offlinePlayer, type);
    }

    /**
     * プレイヤーが既に習得したあるジョブのスキルの数を取得する
     * @return
     */
    public int getLearntSkillCount(OfflinePlayer offlinePlayer, JobData.JobType type){
        UUID uuid = offlinePlayer.getUniqueId();
        List<JobSkill> datas = datasMap.get(uuid);
        if(datas == null){
            datasMap.put(uuid, new ArrayList<>());
            return 0;
        }
        
        List<JobSkill> datasMatchingType = datas.stream().filter(jobSkill -> jobSkill.getLearnedJobType() == type).toList();
        int skillLevelSum = 0;
        for (JobSkill jobSkill : datasMatchingType) {
            skillLevelSum += jobSkill.getLevel();
        }
        return skillLevelSum;
    }

    /**
     * プレイヤーにスキルを1レベル分習得させる
     * @param offlinePlayer プレイヤー名
     * @param jobType スキルを習得したジョブの種類
     * @param skillType スキルの種類
     */
    public void learnSkill(OfflinePlayer offlinePlayer, JobData.JobType jobType, Class<? extends JobSkill> skillType) {
        UUID uuid = offlinePlayer.getUniqueId();
        List<JobSkill> datas = datasMap.get(uuid);
        if(datas == null){
            datas = new ArrayList<>();
            datasMap.put(uuid, datas);
        }

        JobSkill learnedSkillInstance = datas.stream().filter(skillInData -> {
            return skillType.isInstance(skillInData) && skillInData.getLearnedJobType() == jobType;
        }).findFirst().orElse(null);
        
        if(learnedSkillInstance != null){ //既にスキルを習得済みの場合、スキルのレベルを1上げる
            learnedSkillInstance.setLevel(learnedSkillInstance.getLevel() + 1);
            return;
        }

        //スキルを習得していない場合、新しくそのスキルを習得する
        JobSkill newSkillInstance;
        try {
            newSkillInstance = skillType.getDeclaredConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
        newSkillInstance.setLevel(1);
        newSkillInstance.setLearnedJobType(jobType);
        newSkillInstance.setInternalName(skillType.getCanonicalName());
        
        datas.add(newSkillInstance);
    }

    /**
     * プレイヤーが習得することができるあるスキルのレベルを取得する
     */
    public int getSkillLevel(OfflinePlayer offlinePlayer, JobData.JobType jobType, Class<? extends JobSkill> skillType) {
        UUID uuid = offlinePlayer.getUniqueId();
        List<JobSkill> datas = datasMap.get(uuid);
        if(datas == null){
            datas = new ArrayList<>();
            datasMap.put(uuid, datas);
        }

        JobSkill learnedSkillInstance = datas.stream().filter(skillInData -> {
            return skillType.isInstance(skillInData) && skillInData.getLearnedJobType() == jobType;
        }).findFirst().orElse(null);

        if(learnedSkillInstance != null){
            return learnedSkillInstance.getLevel();
        }
        else 
            return 0;
    }

    /**
     * プレイヤーが習得したスキルたちがシリアライズされる際型がJobSkillになってしまうので、internalnameからそれぞれのクラスに戻してあげる
     */
    public void onDeserialize(){
        
        for (Map.Entry<UUID, List<JobSkill>> uuidListEntry : datasMap.entrySet()) {
            List<JobSkill> extendedJobSkills = new ArrayList<>();
            List<JobSkill> jobSkills = uuidListEntry.getValue();
            for (JobSkill jobSkill : jobSkills) {
                String canonicalName = jobSkill.getInternalName();
                try {
                    Class<?> newClass = Class.forName(canonicalName);
                    JobSkill newJobSkill = (JobSkill) newClass.getDeclaredConstructor().newInstance();
                    newJobSkill.setLevel(jobSkill.getLevel());
                    newJobSkill.setLearnedJobType(jobSkill.getLearnedJobType());
                    newJobSkill.setInternalName(jobSkill.getInternalName());
                    extendedJobSkills.add(newJobSkill);
                } catch (ClassNotFoundException | InvocationTargetException | InstantiationException |
                         IllegalAccessException | NoSuchMethodException e) {
                }
            }
            uuidListEntry.setValue(extendedJobSkills);
        }
    }
}
