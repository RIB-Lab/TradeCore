package net.riblab.tradecore.job.skill;

import net.riblab.tradecore.job.data.JobData;
import net.riblab.tradecore.job.data.JobType;
import net.riblab.tradecore.modifier.IModifier;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.function.Consumer;

public interface JobSkillService {

    List<Consumer<Player>> getOnJobSkillChanged();
    /**
     * プレイヤーのJobスキルデータを初期化する
     */
    void resetPlayerJobSkillData(OfflinePlayer offlinePlayer);

    /**
     * プレイヤーがまだ消費していないスキルポイントの数を取得する
     */
    int getUnSpentSkillPoints(OfflinePlayer offlinePlayer, JobType type);

    /**
     * プレイヤーが既に習得したあるジョブのスキルの数を取得する
     *
     * @return
     */
    int getLearntSkillCount(OfflinePlayer offlinePlayer, JobType type);

    /**
     * プレイヤーにスキルを1レベル分習得させる
     *
     * @param offlinePlayer プレイヤー名
     * @param jobType       スキルを習得したジョブの種類
     * @param skillType     スキルの種類
     */
    void learnSkill(OfflinePlayer offlinePlayer, JobType jobType, Class<? extends IJobSkill> skillType);

    /**
     * プレイヤーが習得することができるあるスキルの現在のレベルを取得する
     */
    int getSkillLevel(OfflinePlayer offlinePlayer, JobType jobType, Class<? extends IJobSkill> skillType);

    /**
     * プレイヤーが習得したスキルたちがシリアライズされる際型がJobSkillになってしまうので、internalnameからそれぞれのクラスに戻してあげる
     */
    void onDeserialize();

    /**
     * プレイヤーが習得しているスキルを発動させて、変数を修飾する
     *
     * @param originalValue 修飾したい変数
     * @param modifiedValue 他の要因で既に値が変更された修飾したい変数
     * @param clazz         イベントの種類(インターフェース)
     * @param <T>           変数の型
     * @return 修飾された値
     */
    <T> T apply(Player player, T originalValue, T modifiedValue, Class<? extends IModifier<T>> clazz);
}
