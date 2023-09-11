/*
 * Copyright (c) 2023. RIBLaB
 */
package net.riblab.tradecore.general;

/**
 * システムエラーメッセージ
 */
public enum ErrorMessages {
    ADVANCEMENT_INIT_TWO_TIMES("Advancementが2回初期化されようとしました"),
    TOO_MANY_DUNGEON_INSTANCES("ダンジョンのインスタンス数が1000を超えました。嘘だろ"),
    FAILED_TO_PARSE_ITEM_NAME("表示名の解析に失敗しました："),
    FAILED_TO_PARSE_ITEM_MATERIAL("マテリアルの解析に失敗しました："),
    ILLEGAL_ITEM_MOD_NAME("不正なmod名が検出されました"),
    FAILED_TO_PARSE_ITEM_MOD("アイテムのmodの内容の解析に失敗しました:"),
    CANNOT_FIND_TRACKER("ダンジョンにトラッカーが紐づいていません！"),
    TASK_INIT_TWO_TIMES("このプラグインの常駐タスクが2回初期化されようとしました"),
    PROTOCOLLIB_INIT_TWO_TIMES("ProtocolLibが2回初期化されようとしました"),
    PLAYERSTATS_INIT_TWO_TIMES("PlayerStatsが2回初期化されようとしました"),
    FAILED_TO_PARSE_FILE("ファイルの解析に失敗しました: "),
    INVAILD_DUNGEON_NAME("ダンジョン名からダンジョンを取得できませんでした"),
    FAILED_TO_GENERATE_DUNGEON_WORLD("ダンジョンデータからワールドの生成に失敗しました"),
    INVALID_ARGUMENT("不正な引数: ")
    ;

    private final String message;

    ErrorMessages(String message) {
        this.message = message;
    }

    public String get() {
        return message;
    }
}
