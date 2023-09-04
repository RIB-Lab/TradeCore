package net.riblab.tradecore.job.skill;

import lombok.Getter;
import net.riblab.tradecore.TradeCore;
import net.riblab.tradecore.config.DataService;
import net.riblab.tradecore.job.data.JobDataService;
import net.riblab.tradecore.job.data.JobType;
import net.riblab.tradecore.modifier.IModifier;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.function.Consumer;

/**
 * スキルデータベースの操作クラス
 */
enum JobSkillServiceImpl implements JobSkillService {
    INSTANCE;

    private final Map<UUID, List<IJobSkill>> datasMap = DataService.getImpl().getJobDatas().getPlayerJobSkills();

    /**
     * プレイヤーのJobSkillが変更された時のイベント
     */
    @Getter
    private final List<Consumer<Player>> onJobSkillChanged = new ArrayList<>();

    @Override
    public void resetPlayerJobSkillData(OfflinePlayer offlinePlayer) {
        UUID uuid = offlinePlayer.getUniqueId();
        List<IJobSkill> datas = datasMap.get(uuid);
        if (datas != null)
            datas.clear();
        else {
            datasMap.put(uuid, new ArrayList<>());
        }

        if (offlinePlayer instanceof Player player)
            onJobSkillChanged.forEach(playerConsumer -> playerConsumer.accept(player));
    }

    @Override
    public int getUnSpentSkillPoints(OfflinePlayer offlinePlayer, JobType type) {
        return JobDataService.getImpl().getJobData(offlinePlayer, type).getLevel() / 10 - getLearntSkillCount(offlinePlayer, type);
    }

    @Override
    public int getLearntSkillCount(OfflinePlayer offlinePlayer, JobType type) {
        UUID uuid = offlinePlayer.getUniqueId();
        List<IJobSkill> datas = datasMap.get(uuid);
        if (Objects.isNull(datas)) {
            datasMap.put(uuid, new ArrayList<>());
            return 0;
        }

        List<IJobSkill> datasMatchingType = datas.stream().filter(jobSkill -> jobSkill.getLearnedJobType() == type).toList();
        int skillLevelSum = 0;
        for (IJobSkill IJobSkill : datasMatchingType) {
            skillLevelSum += IJobSkill.getLevel();
        }
        return skillLevelSum;
    }

    @Override
    public void learnSkill(OfflinePlayer offlinePlayer, JobType jobType, Class<? extends IJobSkill> skillType) {
        UUID uuid = offlinePlayer.getUniqueId();
        List<IJobSkill> datas = datasMap.computeIfAbsent(uuid, k -> new ArrayList<>());

        IJobSkill learnedSkillInstance = datas.stream().filter(
                skillInData -> skillType.isInstance(skillInData) && skillInData.getLearnedJobType() == jobType).findFirst().orElse(null);

        if (learnedSkillInstance != null) { //既にスキルを習得済みの場合、スキルのレベルを1上げる
            learnedSkillInstance.setLevel(learnedSkillInstance.getLevel() + 1);

            if (offlinePlayer instanceof Player player)
                onJobSkillChanged.forEach(playerConsumer -> playerConsumer.accept(player));
            return;
        }

        //スキルを習得していない場合、新しくそのスキルを習得する
        IJobSkill newSkillInstance;
        try {
            newSkillInstance = skillType.getDeclaredConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                 NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
        newSkillInstance.setLevel(1);
        newSkillInstance.setLearnedJobType(jobType);
        newSkillInstance.setInternalName(skillType.getCanonicalName());

        datas.add(newSkillInstance);

        if (offlinePlayer instanceof Player player)
            onJobSkillChanged.forEach(playerConsumer -> playerConsumer.accept(player));
    }

    @Override
    public int getSkillLevel(OfflinePlayer offlinePlayer, JobType jobType, Class<? extends IJobSkill> skillType) {
        UUID uuid = offlinePlayer.getUniqueId();
        List<IJobSkill> datas = datasMap.computeIfAbsent(uuid, k -> new ArrayList<>());

        IJobSkill learnedSkillInstance = datas.stream().filter(
                skillInData -> skillType.isInstance(skillInData) && skillInData.getLearnedJobType() == jobType).findFirst().orElse(null);

        if (learnedSkillInstance != null) {
            return learnedSkillInstance.getLevel();
        } else
            return 0;
    }

    @Override
    public void onDeserialize() {
        for (Map.Entry<UUID, List<IJobSkill>> uuidListEntry : datasMap.entrySet()) {
            List<IJobSkill> extendedIJobSkills = new ArrayList<>();
            List<IJobSkill> IJobSkills = uuidListEntry.getValue();
            for (IJobSkill IJobSkill : IJobSkills) {
                String canonicalName = IJobSkill.getInternalName();
                try {
                    Class<?> newClass = Class.forName(canonicalName);
                    IJobSkill newIJobSkill = (IJobSkill) newClass.getDeclaredConstructor().newInstance();
                    newIJobSkill.setLevel(IJobSkill.getLevel());
                    newIJobSkill.setLearnedJobType(IJobSkill.getLearnedJobType());
                    newIJobSkill.setInternalName(IJobSkill.getInternalName());
                    extendedIJobSkills.add(newIJobSkill);
                } catch (ClassNotFoundException | InvocationTargetException | InstantiationException |
                         IllegalAccessException | NoSuchMethodException ignored) {
                }
            }
            uuidListEntry.setValue(extendedIJobSkills);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T apply(Player player, T originalValue, T modifiedValue, Class<? extends IModifier<T>> clazz) {
        UUID uuid = player.getUniqueId();
        List<IJobSkill> datas = datasMap.computeIfAbsent(uuid, k -> new ArrayList<>());

        List<IJobSkill> learnedSkillInstances = datas.stream().filter(clazz::isInstance).toList();
        for (IJobSkill learnedSkillInstance : learnedSkillInstances) {
            modifiedValue = ((IModifier<T>) learnedSkillInstance).apply(originalValue, modifiedValue);
        }

        return modifiedValue;
    }
}
