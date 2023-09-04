package net.riblab.tradecore.general;

import org.bukkit.entity.Player;

/**
 * 攻撃のクールダウンを管理するクラス
 */
public sealed interface AttackCooldownService permits AttackCooldownServiceImpl{
    static AttackCooldownService getImpl(){
        return AttackCooldownServiceImpl.INSTANCE;
    }

    /**
     * プレイヤーにクールダウンを与える
     */
    void add(Player player, double duration);

    /**
     * プレイヤーのクールダウンの秒数を取得する
     * @return クールダウンの秒数。クールダウンがなかったら0
     */
    double getCooldown(Player player);

    /**
     * Removeしたフレームと同時に別のクールダウンを差し込むとRunnableがバグるので注意
     */
    void forceRemove(Player player);
}
