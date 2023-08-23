package net.riblab.tradecore;

import lombok.Data;
import lombok.Getter;

/**
 * あるプレイヤーのこのサーバー内でのステータス
 */
@Data
public class PlayerStats {
    @Getter
    private static final int defaultMaxHP = 20;
    /**
     * 最大HP
     */
    private int maxHp = 20;
    
    @Getter
    private static final float defaultWalkSpeed = 0.2f;
    /**
     * 歩行速度
     */
    private float walkSpeed = 0.2f;
}
