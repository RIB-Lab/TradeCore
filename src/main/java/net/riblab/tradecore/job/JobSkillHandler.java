package net.riblab.tradecore.job;

import net.riblab.tradecore.PlayerStatsHandler;
import net.riblab.tradecore.TradeCore;
import net.riblab.tradecore.modifier.IModifier;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

/**
 * スキルデータベースの操作クラス
 */
public class JobSkillHandler {

    private final Map<UUID, List<JobSkill>> datasMap = TradeCore.getInstance().getConfigManager().getJobDatas().playerJobSkills;

    /**
     * プレイヤーのJobSkilが変更された時のイベント
     */
    public List<Consumer<Player>> onJobSkillChanged = new ArrayList<>();
    
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

        if(offlinePlayer instanceof Player player)
            onJobSkillChanged.forEach(playerConsumer -> playerConsumer.accept(player));
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

            if(offlinePlayer instanceof Player player)
                onJobSkillChanged.forEach(playerConsumer -> playerConsumer.accept(player));
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
        
        if(offlinePlayer instanceof Player player)
            onJobSkillChanged.forEach(playerConsumer -> playerConsumer.accept(player));
    }

    /**
     * プレイヤーが習得することができるあるスキルの現在のレベルを取得する
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

    /**
     * プレイヤーが習得しているスキルを発動させて、変数を修飾する
     * @param originalValue 修飾したい変数
     * @param clazz イベントの種類(インターフェース)
     * @return 修飾された値
     * @param <T> 変数の型
     */
    public <T> T apply(Player player, T originalValue, Class<? extends IModifier<T>> clazz){
        UUID uuid = player.getUniqueId();
        List<JobSkill> datas = datasMap.get(uuid);
        if(datas == null){
            datas = new ArrayList<>();
            datasMap.put(uuid, datas);
        }
        
        List<JobSkill> learnedSkillInstances = datas.stream().filter(clazz::isInstance).toList();
        T modifiedValue = originalValue;
        for (JobSkill learnedSkillInstance : learnedSkillInstances) {
            modifiedValue = ((IModifier<T>) learnedSkillInstance).apply(originalValue, modifiedValue);
        }
        
        return modifiedValue;
    }
}
