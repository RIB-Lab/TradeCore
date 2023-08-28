package net.riblab.tradecore.general;

/**
 * コマンドの引数の説明
 */
public enum CommandArgDescs {
    PLAYER("プレイヤー名"),
    MONEY("設定したいお金"),
    TICKET("設定したいチケット数"),
    TCITEM("アイテム"),
    AMOUNT("数量"),
    MOBNAME("モブの名前"),
    SHOPDATA("ショップの種類"),
    JOBTYPE("職業の種類"),
    LEVEL("レベル"),
    DUNGEONDATA("ダンジョン名"),
    INSTANCEID("インスタンスID。-1で新規作成");

    private final String name;

    CommandArgDescs(String name) {
        this.name = name;
    }
    
    public String get(){
        return name;
    }
}
