/*
 * Copyright (c) 2023. RIBLaB
 */
package net.riblab.tradecore.item.base;

import lombok.Getter;

import java.util.Random;

/**
 * 採掘速度のテーブル(1が素手と同じ速さで、10000000000で1tick破壊)
 */
public enum MiningSpeedTable {
    PEBBLE(1.1, 1.1, 1.1),
    HATCHET(1.15, 1.15, 1.15), //バニラのクラフトシステムを利用するためプロパティを固定する必要がある
    WOODMADE(1.15, 1.2, 1.25),
    STONEMADE(1.20, 1.25, 1.30),
    IRONMADE(1.3, 1.4, 1.5),
    GOLDMADE(1.6, 1.7, 1.8);

    /**
     * 最小採掘速度
     */
    @Getter
    private final double minMiningSpeed;
    /**
     * 中間採掘速度(プレビュー、レシピ表示用)
     */
    @Getter
    private final double middleMiningSpeed;
    /**
     * 最大採掘速度
     */
    @Getter
    private final double maxMiningSpeed;

    MiningSpeedTable(double minMiningSpeed, double middleMiningSpeed, double maxMiningSpeed) {
        this.minMiningSpeed = minMiningSpeed;
        this.middleMiningSpeed = middleMiningSpeed;
        this.maxMiningSpeed = maxMiningSpeed;
    }

    /**
     * テーブルの数値を使って採掘速度を生成する
     */
    public double getRandomMiningSpeed() {
        if (getMinMiningSpeed() == getMaxMiningSpeed())
            return getMiddleMiningSpeed();

        return new Random().nextDouble(getMinMiningSpeed(), getMaxMiningSpeed());
    }
}
