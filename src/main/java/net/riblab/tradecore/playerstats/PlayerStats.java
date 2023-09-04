package net.riblab.tradecore.playerstats;

import lombok.Data;

/**
 * あるプレイヤーのこのサーバー内でのステータス
 */
@Data
final class PlayerStats implements IPlayerStats {
    /**
     * 最大HP
     */
    private int maxHp = defaultMaxHP;
    /**
     * 歩行速度
     */
    private float walkSpeed = defaultWalkSpeed;
    /**
     * 水中呼吸レベル
     */
    private int waterBreatheLevel = defaultWaterBreatheLevel;
}
