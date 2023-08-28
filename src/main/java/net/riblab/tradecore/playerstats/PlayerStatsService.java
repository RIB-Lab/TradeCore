package net.riblab.tradecore.playerstats;

import org.bukkit.entity.Player;

import javax.annotation.ParametersAreNonnullByDefault;

public interface PlayerStatsService {
    static PlayerStatsService getImpl(){
        return new PlayerStatsServiceImpl();
    }
    
    /**
     * 装備やジョブスキルが更新された時、それらでプレイヤーステータスを修飾し、それをプレイヤーに反映する
     */
    @ParametersAreNonnullByDefault
    void update(Player player);

    /**
     * プレイヤーステータスをプレイヤーに反映する
     */
    @ParametersAreNonnullByDefault
    void apply(Player player);

    /**
     * あるプレイヤーのステータスをリストから削除する
     */
    @ParametersAreNonnullByDefault
    void remove(Player player);
}
