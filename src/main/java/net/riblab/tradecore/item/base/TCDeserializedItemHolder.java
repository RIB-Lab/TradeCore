package net.riblab.tradecore.item.base;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

/**
 * yamlで書かれたアイテムをデシリアライズしたものを保持するクラス
 */
public enum TCDeserializedItemHolder {
    INSTANCE;

    /**
     * デシリアライズしたアイテム
     */
    @Getter
    private final List<ITCItem> deserializedItems = new ArrayList<>();//TODO:ゲッターを削除して読み書きをメソッド経由で行うように
}
