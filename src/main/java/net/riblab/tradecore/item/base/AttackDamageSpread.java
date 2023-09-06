/*
 * Copyright (c) 2023. RIBLaB 
 */
package net.riblab.tradecore.item.base;

import lombok.Getter;

import java.util.Random;

/**
 * 武器のtierごとに武器に付与されるランダムアタックダメージがどれだけ拡散するか
 */
public enum AttackDamageSpread {
    WOOD(0.5),
    STONE(1.0),
    IRON(1.5)
    ;

    @Getter
    private final double spreading;

    AttackDamageSpread(double spreading) {
        this.spreading = spreading;
    }

    /**
     * テーブルの値からランダムなアタックダメージを取得する
     * @param damage ランダム化される前のダメージ
     */
    public double getRandomDamage(double damage){
        return new Random().nextDouble(spreading * 2) - spreading + damage;
    }
}
