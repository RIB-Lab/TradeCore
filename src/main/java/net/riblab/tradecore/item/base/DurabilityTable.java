package net.riblab.tradecore.item.base;

import lombok.Getter;

/**
 * アイテムの耐久値テーブル
 */
public enum DurabilityTable {
    INFINITE(-1,-1,-1),
    HATCHET(8, 10, 12),
    WOODENAGE(25, 32, 39),
    STONEAGE(108, 128, 148),
    IRONAGE(452, 512, 572);

    /**
     * このテーブルに属するアイテムが取る最小の最大耐久値
     */
    @Getter
    private final int minMaxDurability;

    /**
     * このテーブルに属するアイテムが取る最大耐久値の中間値(表示用)
     */
    @Getter
    private final int middleMaxDurability;

    /**
     * このテーブルに属するアイテムが取る最大の最大耐久値
     */
    @Getter
    private final int maxMaxDurability;

    DurabilityTable(int minMaxDurability, int middleMaxDurability, int maxMaxDurability) {
        this.minMaxDurability = minMaxDurability;
        this.middleMaxDurability = middleMaxDurability;
        this.maxMaxDurability = maxMaxDurability;
    }
}
