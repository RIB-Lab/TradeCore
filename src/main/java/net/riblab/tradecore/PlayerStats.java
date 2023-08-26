package net.riblab.tradecore;

import lombok.Data;
import lombok.Getter;

/**
 * あるプレイヤーのこのサーバー内でのステータス
 */
@Data
public class PlayerStats implements IPlayerStats {
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
