package net.riblab.tradecore.playerstats;

import org.bukkit.entity.Player;

public interface PlayerStatsService {
    /**
     * 装備やジョブスキルが更新された時、それらでプレイヤーステータスを修飾し、それをプレイヤーに反映する
     */
    void update(Player player);

    IPlayerStats get(Player player);

    /**
     * プレイヤーステータスをプレイヤーに反映する
     */
    void apply(Player player);

    /**
     * あるプレイヤーのステータスをリストから削除する
     */
    void remove(Player player);
}
