package net.riblab.tradecore.dungeon;

/**
 * ダンジョンの名前達。ダンジョンを参照するときこれを参照することでenumの初期化エラーを解消できる
 */
public enum DungeonNames {
    TEST("test"),
    STONEROOM("stoneroom");
    
    private final String name;

    DungeonNames(String name) {
        this.name = name;
    }
    
    public String get(){
        return name;
    }
}
