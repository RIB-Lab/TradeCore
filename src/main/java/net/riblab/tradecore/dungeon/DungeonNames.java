/*
 * Copyright (c) 2023. RIBLaB
 */
package net.riblab.tradecore.dungeon;

import lombok.Getter;

/**
 * ダンジョンの名前達。ダンジョンを参照するときこれを参照することでenumの初期化エラーを解消できる
 */
public enum DungeonNames {
    TEST("test", "テスト"),
    STONE_ROOM("stoneroom", "石の部屋");

    @Getter
    private final String internalName;

    @Getter
    private final String displayName;

    DungeonNames(String internalName, String displayName) {
        this.internalName = internalName;
        this.displayName = displayName;
    }
}
